package com.bsuir.distcomp.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String login;
    private String password;
    private String firstname;
    private String lastname;
    private String role;
}