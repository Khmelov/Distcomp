package com.example.publisher.dto.response;

import lombok.Data;

@Data
public class AuthorResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
    private String role;
}