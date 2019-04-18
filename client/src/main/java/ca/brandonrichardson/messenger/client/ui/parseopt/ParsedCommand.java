package ca.brandonrichardson.messenger.client.ui.parseopt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParsedCommand {

    private String command;

    private String[] commandArguments;
}
