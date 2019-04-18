package ca.brandonrichardson.messenger.client.session;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerDetails {

    private String serverAddress;

    private int serverPortNumber;
}
