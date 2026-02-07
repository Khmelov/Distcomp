package com.example.demo.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class AuthorRequestTo {
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}
