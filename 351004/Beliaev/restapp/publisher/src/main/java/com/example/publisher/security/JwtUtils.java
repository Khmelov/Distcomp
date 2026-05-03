package com.example.publisher.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Секретный ключ (в реальном проекте выносить в application.properties)
    private final Key key = Keys.hmacShaKeyFor("MySuperSecretKeyForJwtTokenGeneration12345!".getBytes());
    private final int jwtExpirationMs = 86400000; // 24 часа

    public String generateJwtToken(String login, String role) {
        return Jwts.builder()
                .setSubject(login) // sub
                .claim("role", role) // кастомный claim role
                .setIssuedAt(new Date()) // iat
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // exp
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getLoginFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}