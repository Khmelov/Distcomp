package com.aitor.publisher.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class UserResponseTo {
    private long id;
    private String login;
    private String firstname;
    private String lastname;
}
