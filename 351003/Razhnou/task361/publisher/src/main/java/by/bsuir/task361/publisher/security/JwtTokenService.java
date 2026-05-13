package by.bsuir.task361.publisher.security;

import by.bsuir.task361.publisher.dto.response.LoginResponseTo;
import by.bsuir.task361.publisher.entity.User;
import by.bsuir.task361.publisher.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtTokenService {
    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtTokenService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    public LoginResponseTo generateLoginResponse(User user) {
        Instant now = Instant.now();
        String token = Jwts.builder()
                .subject(user.getLogin())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .claim("role", user.getRole().name())
                .signWith(secretKey)
                .compact();
        return new LoginResponseTo(token, "Bearer", user.getRole(), expirationSeconds);
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractLogin(String token) {
        return extractClaims(token).getSubject();
    }

    public UserRole extractRole(String token) {
        return UserRole.valueOf(extractClaims(token).get("role", String.class));
    }
}
