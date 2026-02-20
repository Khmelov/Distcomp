package com.example.lab.dto;

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
    private final String firstName;

    @NotBlank
    @Size(min = 2, max = 64)
    private final String lastName;

    @JsonCreator
    public UserRequestTo(
            @JsonProperty("login") String login,
            @JsonProperty("password") String password,
            @JsonProperty("firstname") String firstName,
            @JsonProperty("lastname") String lastName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}