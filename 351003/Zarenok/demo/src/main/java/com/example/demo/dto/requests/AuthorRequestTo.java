package com.example.demo.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthorRequestTo {
    @NotBlank
    @Email
    @Size(min = 5, max = 100)
    @JsonProperty("login")
    private String login;

    @NotBlank
    @Size(min = 5, max = 100)
    @JsonProperty("password")
    private String password;

    @NotBlank
    @Size(min = 2, max = 50)
    @JsonProperty("firstname")
    private String firstname;

    @NotBlank
    @Size(min = 2, max = 50)
    @JsonProperty("lastname")
    private String lastname;
}
