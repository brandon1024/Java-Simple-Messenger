package ca.brandonrichardson.messenger.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Message extends TransportEntity {

    private String senderUsername;

    private String message;
}
