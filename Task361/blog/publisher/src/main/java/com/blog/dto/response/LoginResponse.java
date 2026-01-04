package com.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType = "Bearer";

    @JsonProperty("role")
    private String role;

    @JsonProperty("login")
    private String login;

    @JsonProperty("expires_in")
    private Long expiresIn;

    // Конструкторы
    public LoginResponse() {}

    public LoginResponse(String accessToken, String role, String login, Long expiresIn) {
        this.accessToken = accessToken;
        this.role = role;
        this.login = login;
        this.expiresIn = expiresIn;
    }

    // Геттеры и сеттеры
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}