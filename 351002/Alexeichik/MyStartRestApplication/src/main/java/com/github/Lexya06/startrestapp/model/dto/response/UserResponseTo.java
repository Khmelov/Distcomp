package com.github.Lexya06.startrestapp.model.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponseTo {
    Long id;
    String login;
    String password;
    String firstname;
    String lastname;
}
