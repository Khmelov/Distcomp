package com.apigateway.security;

public record LoginResponse(String access_token, String token_type) {
}
