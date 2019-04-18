package ca.brandonrichardson.messenger.client.core;

import ca.brandonrichardson.messenger.client.core.connection.ConnectionListener;
import ca.brandonrichardson.messenger.client.session.Session;
import ca.brandonrichardson.messenger.client.ui.AuthenticatedInterface;
import ca.brandonrichardson.messenger.client.ui.Interface;
import ca.brandonrichardson.messenger.client.ui.LoginInterface;
import ca.brandonrichardson.messenger.client.ui.parseopt.ParsedCommand;
import ca.brandonrichardson.messenger.client.ui.SimpleInterface;
import ca.brandonrichardson.messenger.common.dto.Message;
import ca.brandonrichardson.messenger.common.dto.TransportEntity;
import ca.brandonrichardson.messenger.common.dto.builder.TransportEntityBuilder;
import ca.brandonrichardson.messenger.common.keygen.KeyGenerator;
import ca.brandonrichardson.messenger.common.keygen.strategy.HashGeneratorStrategy;
import ca.brandonrichardson.messenger.common.keygen.strategy.RandomGeneratorStrategy;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public class MessengerClient {

    private Interface consoleInterface;

    private boolean isAuthenticated;

    private Session userSession;

    public MessengerClient() {
        this.consoleInterface = new LoginInterface(new SimpleInterface());
        this.isAuthenticated = false;
        this.userSession = new Session();

        ConnectionListener.getInstance().registerObserver(error -> {
            if(!error) {
                System.out.println("Your connection has been terminated due to an unexpected error.");
                System.exit(1);
            }
        });
    }

    public void start() {
        System.out.println(String.format("%s\n%s\n%s",
                "SWE4403 Software Architecture and Design Patterns",
                "Final Project: Messenger Client Application",
                "Author: Brandon Richardson"
        ));

        System.out.println();
        this.consoleInterface.prompt();

        while(true) {
            System.out.print("> ");
            System.out.flush();

            String line = this.consoleInterface.readLine().orElse("");
            if(line.isEmpty()) {
                continue;
            }

            Optional<ParsedCommand> command = this.consoleInterface.read(line);
            if(!command.isPresent()) {
                System.err.println(String.format("Unknown command '%s'", line));
                continue;
            }

            switch(command.get().getCommand()) {
                case "help":
                    this.consoleInterface.prompt();
                    continue;
                case "exit":
                    System.exit(0);
            }

            if(this.isAuthenticated) {
                switch(command.get().getCommand()) {
                    case "read":
                        this.read();
                        break;
                    case "send":
                        this.send(command.get().getCommandArguments()[0]);
                        break;
                    case "logout":
                        this.logout();
                        break;
                }
            } else {
                String username = command.get().getCommandArguments()[0];
                String serverIP = command.get().getCommandArguments()[1];
                String portNumber = command.get().getCommandArguments()[2];
                this.login(username, serverIP, portNumber);
            }
        }
    }

    private void login(final String username, final String ip, final String portNumber) {
        try {
            String temporary = KeyGenerator.generateKey(16, RandomGeneratorStrategy.alphanumeric());
            String key = KeyGenerator.generateKey(temporary::getBytes, HashGeneratorStrategy.SHA1HashStrategy());

            this.userSession.setUsername(username);
            this.userSession.setSessionKey(key);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Unable to generate key using SHA1HashStrategy.");
            System.exit(1);
        }

        ConnectionListener.getInstance().start(this.userSession, ip, Integer.parseInt(portNumber));

        this.isAuthenticated = true;
        this.consoleInterface = new AuthenticatedInterface(new SimpleInterface());

        System.out.println(String.format("Hello %s, you are now logged in.\n", username));
        this.consoleInterface.prompt();
    }

    private void logout() {
        this.consoleInterface = new LoginInterface(new SimpleInterface());
        this.userSession = new Session();
        this.isAuthenticated = false;

        ConnectionListener.getInstance().suspend();

        System.out.println("You are now logged out.\n");
        this.consoleInterface.prompt();
    }

    private void read() {
        List<TransportEntity> messages = ConnectionListener.getInstance().getMessages();
        for(TransportEntity message : messages) {
            if(message instanceof Message) {
                Message currentMessage = (Message)message;
                System.out.printf("%s %s - %s\n", message.getTimestamp(), currentMessage.getSenderUsername(), currentMessage.getMessage());
            }
        }
    }

    private void send(final String message) {
        Message messageEntity = TransportEntityBuilder.message()
                .setMessage(message)
                .setUsername(this.userSession.getUsername())
                .setSessionKey(this.userSession.getSessionKey())
                .build();

        try {
            ConnectionListener.getInstance().send(messageEntity);
        } catch (IOException e) {
            System.err.println("Unable to send message due to unexpected IOException.");
            System.exit(1);
        }
    }
}
