package com.distcomp.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(
        "very-secret-key-at-least-32-chars!!".toByteArray()
    )
    private val expirationMs = 86_400_000L

    fun generateToken(login: String, role: String): String {
        val now = Date()
        return Jwts.builder()
            .subject(login)
            .claim("role", role)
            .issuedAt(now)
            .expiration(Date(now.time + expirationMs))
            .signWith(secretKey)
            .compact()
    }

    fun getLogin(token: String): String =
        getClaims(token).subject

    fun getRole(token: String): String =
        getClaims(token).get("role", String::class.java)

    fun isValid(token: String): Boolean = runCatching {
        getClaims(token)
        true
    }.getOrDefault(false)

    private fun getClaims(token: String) =
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload
}