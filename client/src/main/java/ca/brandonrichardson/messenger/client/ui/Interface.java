package ca.brandonrichardson.messenger.client.ui;

import ca.brandonrichardson.messenger.client.ui.parseopt.CommandUsageDescription;
import ca.brandonrichardson.messenger.client.ui.parseopt.ParsedCommand;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public abstract class Interface implements Decorator {

    private static Map<String, CommandUsageDescription> commands;

    private final Decorator decorator;

    private Scanner input;

    static {
        commands = Map.of(
                "exit", new CommandUsageDescription("exit","Exit the application."),
                "help", new CommandUsageDescription("help","Show this help message.")
        );
    }

    public Interface(final Decorator decorator) {
        this.decorator = decorator;
        this.input = new Scanner(System.in);
    }

    @Override
    public void prompt() {
        decorator.prompt();

        commands.forEach((K, V) -> {
            System.out.println(V);
        });

        System.out.println();
    }

    @Override
    public Optional<ParsedCommand> read(final String line) {
        Optional<ParsedCommand> args = decorator.read(line);
        if(args.isPresent()) {
            return args;
        }

        String[] arguments = line.split(" ");
        if(arguments.length == 0) {
            return Optional.empty();
        }

        String command = arguments[0];
        arguments = Arrays.copyOfRange(arguments, 1, arguments.length);
        switch(command) {
            case "exit":
            case "help":
                return Optional.of(new ParsedCommand(command, arguments));
        }

        return Optional.empty();
    }

    public Optional<String> readLine() {
        if(input.hasNextLine()) {
            return Optional.of(input.nextLine());
        }

        return Optional.empty();
    }
}
