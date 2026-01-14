package com.distcomp.publisher.security.dto;

public class TokenResponse {

    private String access_token;

    public TokenResponse() {
    }

    public TokenResponse(String access_token) {
        this.access_token = access_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
