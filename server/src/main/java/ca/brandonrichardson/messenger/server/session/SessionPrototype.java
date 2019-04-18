package ca.brandonrichardson.messenger.server.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SessionPrototype implements Cloneable {

    private String username;

    private String sessionKey;

    public SessionPrototype clone() {
        return new SessionPrototype(this.username, this.sessionKey);
    }
}
