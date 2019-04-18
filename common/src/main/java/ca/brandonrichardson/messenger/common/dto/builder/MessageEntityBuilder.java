package ca.brandonrichardson.messenger.common.dto.builder;

import ca.brandonrichardson.messenger.common.dto.Message;
import ca.brandonrichardson.messenger.common.dto.TransportEntity;

public final class MessageEntityBuilder {

    private Message message;

    public MessageEntityBuilder(final Message message) {
        this.message = message;
        this.message.setType(TransportEntity.EntityType.MESSAGE);
    }

    public MessageEntityBuilder setUsername(final String username) {
        this.message.setSenderUsername(username);
        return this;
    }

    public MessageEntityBuilder setSessionKey(final String sessionKey) {
        this.message.setSessionKey(sessionKey);
        return this;
    }

    public MessageEntityBuilder setMessage(final String message) {
        this.message.setMessage(message);
        return this;
    }

    public Message build() {
        return this.message;
    }
}
