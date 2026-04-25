package com.example.demo.models;

import lombok.Data;

@Data
public class Writer extends BaseEntity{
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}
