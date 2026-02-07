package com.example.demo.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class AuthorResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}
