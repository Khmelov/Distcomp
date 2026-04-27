package com.example.lab.publisher.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequestTo {

    @NotBlank
    @Size(min = 2, max = 64)
    private final String login;

    @NotBlank
    @Size(min = 8, max = 128)
    private final String password;

    @NotBlank
    @Size(min = 2, max = 64)
    private final String firstname;

    @NotBlank
    @Size(min = 2, max = 64)
    private final String lastname;

    @JsonCreator
    public UserRequestTo(
            @JsonProperty("login") String login,
            @JsonProperty("password") String password,
            @JsonProperty("firstname") String firstname,
            @JsonProperty("lastname") String lastname) {
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
}