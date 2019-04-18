package ca.brandonrichardson.messenger.client.ui.parseopt;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class CommandUsageDescription {

    private String command;

    private List<String> arguments;

    private String description;

    public CommandUsageDescription(final String command, final String description) {
        this(command, Collections.emptyList(), description);
    }

    public CommandUsageDescription(final String command, final List<String> arguments, final String description) {
        this.command = command;
        this.arguments = arguments;
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(command);

        for(String arg : arguments) {
            sb.append(" ");
            sb.append(arg);
        }

        sb.append("\n\t");
        sb.append(description);

        return sb.toString();
    }
}
