package com.example.restapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Author extends BaseEntity {
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}