package by.bsuir.task361.publisher.security;

import by.bsuir.task361.publisher.dto.response.LoginResponseTo;
import by.bsuir.task361.publisher.entity.User;
import by.bsuir.task361.publisher.entity.UserRole;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenServiceTest {

    @Test
    void generatedTokenContainsRequiredClaims() {
        JwtTokenService jwtTokenService = new JwtTokenService(
                "task361-jwt-secret-task361-jwt-secret-1234567890",
                3600
        );
        User user = new User(7L, "admin", "$2a$10$encoded", UserRole.ADMIN, "Ivan", "Ivanov");

        LoginResponseTo response = jwtTokenService.generateLoginResponse(user);
        Claims claims = jwtTokenService.extractClaims(response.accessToken());

        assertNotNull(response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals("admin", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
        assertTrue(claims.getIssuedAt().before(Date.from(Instant.now().plusSeconds(5))));
    }
}
