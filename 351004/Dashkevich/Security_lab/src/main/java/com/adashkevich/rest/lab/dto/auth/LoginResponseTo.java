package com.adashkevich.rest.lab.dto.auth;

public class LoginResponseTo {
    public String access_token;
    public String type_token = "Bearer";

    public LoginResponseTo(String accessToken) {
        this.access_token = accessToken;
    }
}
