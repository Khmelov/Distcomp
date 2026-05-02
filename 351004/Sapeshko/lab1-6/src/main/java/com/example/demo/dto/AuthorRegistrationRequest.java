package com.example.demo.dto;

import lombok.Data;

@Data
public class AuthorRegistrationRequest {
    private String login;
    private String password;
    private String firstname;
    private String lastname;
    private String role;
}