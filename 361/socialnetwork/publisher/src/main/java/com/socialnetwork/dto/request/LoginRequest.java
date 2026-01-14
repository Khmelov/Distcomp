package com.socialnetwork.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @Size(min = 2, max = 64, message = "Login must be between 2 and 64 characters")
    @NotNull(message = "Login is required")
    private String login;

    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @NotNull(message = "Password is required")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
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
}

