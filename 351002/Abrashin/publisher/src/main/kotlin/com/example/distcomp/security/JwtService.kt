package com.example.distcomp.security

import com.example.distcomp.model.Creator
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${security.jwt.secret}") secret: String,
    @Value("\${security.jwt.expiration-ms}") private val expirationMs: Long
) {
    private val signingKey: SecretKey = Keys.hmacShaKeyFor(resolveKey(secret))

    fun generateToken(creator: Creator): String {
        val now = Date()
        val expiresAt = Date(now.time + expirationMs)
        return Jwts.builder()
            .subject(creator.login ?: throw IllegalArgumentException("Creator login is required for JWT"))
            .issuedAt(now)
            .expiration(expiresAt)
            .claim("role", creator.role.name)
            .signWith(signingKey)
            .compact()
    }

    fun extractLogin(token: String): String = extractAllClaims(token).subject

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val claims = extractAllClaims(token)
        val tokenRole = claims["role"]?.toString()
        val currentRole = userDetails.authorities.firstOrNull()?.authority?.removePrefix("ROLE_")
        return claims.subject == userDetails.username &&
            !claims.expiration.before(Date()) &&
            tokenRole == currentRole
    }

    private fun extractAllClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload

    private fun resolveKey(secret: String): ByteArray =
        runCatching { Decoders.BASE64.decode(secret) }
            .getOrElse { secret.toByteArray(Charsets.UTF_8) }
}
