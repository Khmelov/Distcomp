package com.example.discussion.dto.response;

import lombok.Data;

@Data
public class AuthorResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}