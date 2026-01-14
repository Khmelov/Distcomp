package com.socialnetwork.dto.response;

public class LoginResponse {
    private String access_token;
    private String type_token = "Bearer";

    public LoginResponse() {}

    public LoginResponse(String access_token) {
        this.access_token = access_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getType_token() {
        return type_token;
    }

    public void setType_token(String type_token) {
        this.type_token = type_token;
    }
}

