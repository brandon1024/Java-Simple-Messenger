package ca.brandonrichardson.messenger.client.ui;

import ca.brandonrichardson.messenger.client.ui.parseopt.CommandUsageDescription;
import ca.brandonrichardson.messenger.client.ui.parseopt.ParsedCommand;
import ca.brandonrichardson.messenger.client.ui.parseopt.UsageDescriptionList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LoginInterface extends Interface {

    private static UsageDescriptionList commands;

    static {
        commands = UsageDescriptionList.of(
                new CommandUsageDescription("login", List.of("<username>", "<server ip>", "<server port>"), "Log into the server with the given IP address and the given username")
        );
    }

    public LoginInterface(final Decorator decorator) {
        super(decorator);
    }

    @Override
    public void prompt() {
        System.out.println("Available Commands:");

        for(CommandUsageDescription command : commands) {
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

        for(CommandUsageDescription command : commands) {
            if(command.getCommand().equals(arguments[0])) {
                arguments = Arrays.copyOfRange(arguments, 1, arguments.length);
                return Optional.of(new ParsedCommand(command.getCommand(), arguments));
            }
        }

        return super.read(line);
    }
}
