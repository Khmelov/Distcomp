package com.task310.socialnetwork.dto.request;

import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRequestTo {
    @Size(min = 2, max = 64, message = "Login must be between 2 and 64 characters")
    @JsonProperty("login")
    private String login;

    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @JsonProperty("password")
    private String password;

    @Size(min = 2, max = 64, message = "Firstname must be between 2 and 64 characters")
    @JsonProperty("firstname")
    private String firstname;

    @Size(min = 2, max = 64, message = "Lastname must be between 2 and 64 characters")
    @JsonProperty("lastname")
    private String lastname;

    public UserRequestTo() {}

    public UserRequestTo(String login, String password, String firstname, String lastname) {
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}