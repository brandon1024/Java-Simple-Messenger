package ca.brandonrichardson.messenger.client.ui;

import ca.brandonrichardson.messenger.client.ui.parseopt.ParsedCommand;

import java.util.Optional;

public interface Decorator {

    default void prompt() {

    }

    default Optional<ParsedCommand> read(String line) {
        return Optional.empty();
    }
}
