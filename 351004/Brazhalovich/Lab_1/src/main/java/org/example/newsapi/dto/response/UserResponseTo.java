package org.example.newsapi.dto.response;

import lombok.Data;

@Data
public class UserResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}