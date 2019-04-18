package ca.brandonrichardson.messenger.client.ui;

import ca.brandonrichardson.messenger.client.ui.parseopt.CommandUsageDescription;
import ca.brandonrichardson.messenger.client.ui.parseopt.ParsedCommand;
import ca.brandonrichardson.messenger.client.ui.parseopt.UsageDescriptionIterator;
import ca.brandonrichardson.messenger.client.ui.parseopt.UsageDescriptionList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuthenticatedInterface extends Interface {

    private static UsageDescriptionList commands;

    static {
        commands = UsageDescriptionList.of(
                new CommandUsageDescription("read", "Show any new messages received."),
                new CommandUsageDescription("send", List.of("<message>"), "Send a new message."),
                new CommandUsageDescription("logout","Gracefully log out of this session.")
        );
    }

    public AuthenticatedInterface(final Decorator decorator) {
        super(decorator);
    }

    @Override
    public void prompt() {
        System.out.println("Available Commands:");

        for(CommandUsageDescription command : AuthenticatedInterface.commands) {
            System.out.println(command);
        }

        super.prompt();
    }

    @Override
    public Optional<ParsedCommand> read(final String line) {
        String[] arguments = line.split(" ");
        if(arguments.length == 0) {
            return Optional.empty();
        }

        for(CommandUsageDescription command : AuthenticatedInterface.commands) {
            if(command.getCommand().equals(arguments[0])) {
                if(command.getCommand().equals("send")) {
                    arguments = new String[]{String.join(" ", arguments)};
                } else {
                    arguments = Arrays.copyOfRange(arguments, 1, arguments.length);
                }

                return Optional.of(new ParsedCommand(command.getCommand(), arguments));
            }
        }

        return super.read(line);
    }
}
