package ca.brandonrichardson.messenger.common.dto.builder;

import ca.brandonrichardson.messenger.common.dto.Authentication;
import ca.brandonrichardson.messenger.common.dto.TransportEntity;

public final class AuthenticationEntityBuilder {

    private Authentication auth;

    public AuthenticationEntityBuilder(final Authentication auth) {
        this.auth = auth;
        this.auth.setType(TransportEntity.EntityType.AUTH);
    }

    public AuthenticationEntityBuilder setUsername(final String username) {
        this.auth.setUsername(username);
        return this;
    }

    public AuthenticationEntityBuilder setSessionKey(final String sessionKey) {
        this.auth.setSessionKey(sessionKey);
        return this;
    }

    public Authentication build() {
        return this.auth;
    }
}
