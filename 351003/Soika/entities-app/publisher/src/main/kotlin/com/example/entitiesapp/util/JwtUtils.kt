package com.example.entitiesapp.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtils {
    private val secret = "my_very_secret_key_for_jwt_must_be_long_enough_32_chars"
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())
    private val expirationMs = 3600000

    fun generateToken(login: String, role: String): String {
        val now = Date()
        return Jwts.builder()
            .subject(login)
            .claim("role", role)
            .issuedAt(now)
            .expiration(Date(now.time + expirationMs))
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getLoginFromToken(token: String): String {
        return Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).payload.subject
    }
}