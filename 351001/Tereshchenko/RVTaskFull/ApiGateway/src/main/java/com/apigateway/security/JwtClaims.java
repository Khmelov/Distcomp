package com.apigateway.security;

public record JwtClaims(String login, String role) {
}
