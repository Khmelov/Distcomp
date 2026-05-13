package org.rv.lab1.security;

import org.rv.lab1.domain.Editor;
import org.rv.lab1.domain.EditorRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs
    ) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String createAccessToken(Editor editor) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(expirationMs);
        return Jwts.builder()
                .subject(editor.getLogin())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("role", editor.getRole().name())
                .claim("eid", editor.getId())
                .signWith(key)
                .compact();
    }

    public Claims parseAndVerify(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
