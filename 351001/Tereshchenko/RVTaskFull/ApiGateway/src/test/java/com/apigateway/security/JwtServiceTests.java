package com.apigateway.security;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTests {

    @Test
    void createsTokenWithLoginAndRoleClaims() {
        JwtService jwtService = new JwtService(new ObjectMapper(), "test-secret", 3600);

        String token = jwtService.createToken("writer1", "CUSTOMER");
        JwtClaims claims = jwtService.validate(token);

        assertThat(claims.login()).isEqualTo("writer1");
        assertThat(claims.role()).isEqualTo("CUSTOMER");
    }
}
