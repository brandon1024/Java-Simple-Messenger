package ca.brandonrichardson.messenger.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class TransportEntity implements Serializable {

    private String timestamp;

    private String sessionKey;

    private EntityType type;

    public enum EntityType {
        AUTH,
        MESSAGE
    }
}
