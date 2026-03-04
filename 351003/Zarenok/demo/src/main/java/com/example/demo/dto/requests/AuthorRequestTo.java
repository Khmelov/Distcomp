package com.example.demo.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthorRequestTo {
    @NotBlank
    @Size(min = 2, max = 64)
    @JsonProperty("login")
    private String login;

    @NotBlank
    @Size(min = 2, max = 128)
    @JsonProperty("password")
    private String password;

    @NotBlank
    @Size(min = 2, max = 64)
    @JsonProperty("firstname")
    private String firstname;

    @NotBlank
    @Size(min = 2, max = 64)
    @JsonProperty("lastname")
    private String lastname;
}
