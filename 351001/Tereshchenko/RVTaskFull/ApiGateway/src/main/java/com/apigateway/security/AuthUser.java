package com.apigateway.security;

public record AuthUser(Long id, String login, String firstname, String lastname, String role) {

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
