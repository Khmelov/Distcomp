package com.apigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final long expirationSeconds;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.expiration-seconds:3600}") long expirationSeconds) {
        this.objectMapper = objectMapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
    }

    public String createToken(String login, String role) {
        try {
            long issuedAt = Instant.now().getEpochSecond();
            long expiresAt = issuedAt + expirationSeconds;

            String header = encodeJson(Map.of(
                    "alg", "HS256",
                    "typ", "JWT"
            ));
            String payload = encodeJson(Map.of(
                    "sub", login,
                    "iat", issuedAt,
                    "exp", expiresAt,
                    "role", role
            ));

            String unsignedToken = header + "." + payload;
            return unsignedToken + "." + sign(unsignedToken);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create JWT token", e);
        }
    }

    public JwtClaims validate(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT format");
            }

            String unsignedToken = parts[0] + "." + parts[1];
            String expectedSignature = sign(unsignedToken);
            if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
                throw new IllegalArgumentException("Invalid JWT signature");
            }

            JsonNode payload = objectMapper.readTree(Base64.getUrlDecoder().decode(parts[1]));
            long expiresAt = payload.path("exp").asLong(0);
            if (expiresAt <= Instant.now().getEpochSecond()) {
                throw new IllegalArgumentException("JWT token expired");
            }

            String login = payload.path("sub").asText(null);
            String role = payload.path("role").asText(null);
            if (login == null || role == null) {
                throw new IllegalArgumentException("JWT token misses required claims");
            }

            return new JwtClaims(login, role);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    private String encodeJson(Map<String, ?> value) throws Exception {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(objectMapper.writeValueAsBytes(value));
    }

    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }
}
