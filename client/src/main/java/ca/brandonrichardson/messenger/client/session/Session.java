package ca.brandonrichardson.messenger.client.session;

import lombok.Data;

@Data
public class Session {

    private String username;

    private String sessionKey;
}
