package com.example.publisher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}
