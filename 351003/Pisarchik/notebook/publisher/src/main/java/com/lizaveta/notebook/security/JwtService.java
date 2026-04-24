package com.lizaveta.notebook.security;

import com.lizaveta.notebook.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret-base64:}") final String base64Secret,
            @Value("${app.jwt.secret:}") final String rawSecret,
            @Value("${app.jwt.expiration-ms:86400000}") final long expirationMs) {
        this.expirationMs = Math.max(60_000L, expirationMs);
        this.signingKey = resolveKey(base64Secret, rawSecret);
    }

    private static SecretKey resolveKey(final String base64Secret, final String rawSecret) {
        if (base64Secret != null && !base64Secret.isBlank()) {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret.trim()));
        }
        if (rawSecret == null || rawSecret.getBytes().length < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 32 bytes for HMAC-SHA256");
        }
        return Keys.hmacShaKeyFor(rawSecret.getBytes());
    }

    public String buildAccessToken(final String login, final UserRole userRole) {
        Objects.requireNonNull(login, "login");
        Objects.requireNonNull(userRole, "userRole");
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(login)
                .claim("role", userRole.name())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseValidClaimsOrThrow(final String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isExpiredException(final Exception ex) {
        return ex instanceof ExpiredJwtException
                || ex.getCause() instanceof ExpiredJwtException;
    }
}
