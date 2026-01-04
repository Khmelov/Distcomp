package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Login is required")
    private String login;

    @NotBlank(message = "Password is required")
    private String password;

    // Конструкторы
    public LoginRequest() {}

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // Геттеры и сеттеры
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