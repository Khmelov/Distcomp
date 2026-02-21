package com.example.lab.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponseTo {

    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;

    @JsonCreator
    public UserResponseTo(
            @JsonProperty("id") Long id,
            @JsonProperty("login") String login,
            @JsonProperty("password") String password,
            @JsonProperty("firstname") String firstName,
            @JsonProperty("lastname") String lastName) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
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
