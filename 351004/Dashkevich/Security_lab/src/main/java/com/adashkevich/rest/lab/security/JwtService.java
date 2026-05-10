package com.adashkevich.rest.lab.security;

import com.adashkevich.rest.lab.model.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JwtService {
    private static final String SECRET = "SecurityLabJwtSecretKeyForTask361AtLeast32Bytes";
    private static final long EXPIRATION_SECONDS = 60 * 60 * 24;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String generateToken(String login, Role role) {
        long now = Instant.now().getEpochSecond();
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", login);
        payload.put("iat", now);
        payload.put("exp", now + EXPIRATION_SECONDS);
        payload.put("role", role.name());

        String headerPart = base64Url(toJson(header));
        String payloadPart = base64Url(toJson(payload));
        String unsigned = headerPart + "." + payloadPart;
        return unsigned + "." + sign(unsigned);
    }

    public JwtUser parseAndValidate(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            String unsigned = parts[0] + "." + parts[1];
            if (!constantTimeEquals(sign(unsigned), parts[2])) {
                return null;
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> payload = OBJECT_MAPPER.readValue(payloadJson, new TypeReference<>() {});
            long exp = ((Number) payload.get("exp")).longValue();
            if (exp < Instant.now().getEpochSecond()) {
                return null;
            }

            String login = String.valueOf(payload.get("sub"));
            Role role = Role.valueOf(String.valueOf(payload.get("role")));
            return new JwtUser(login, role);
        } catch (Exception ex) {
            return null;
        }
    }

    private static String toJson(Map<String, Object> value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot create JWT", ex);
        }
    }

    private static String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot sign JWT", ex);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        return java.security.MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }

    public record JwtUser(String login, Role role) {}
}
