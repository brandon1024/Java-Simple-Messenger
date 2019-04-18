package ca.brandonrichardson.messenger.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Authentication extends TransportEntity {

    private String username;
}
