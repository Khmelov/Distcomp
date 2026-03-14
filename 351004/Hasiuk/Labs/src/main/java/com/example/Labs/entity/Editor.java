package com.example.Labs.entity;

import lombok.Data;

@Data
public class Editor {
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}