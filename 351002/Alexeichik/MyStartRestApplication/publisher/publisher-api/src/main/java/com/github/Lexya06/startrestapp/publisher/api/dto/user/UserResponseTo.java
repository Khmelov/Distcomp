package com.github.Lexya06.startrestapp.publisher.api.dto.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseTo {
    Long id;
    String login;
    String password;
    String firstname;
    String lastname;
    String role;
}
