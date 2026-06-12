package by.bsuir.distcomp.security;

import by.bsuir.distcomp.model.UserRole;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private static final String SECRET = "distcomp-task361-secret-key-for-local-lab";
    private static final long TTL_SECONDS = 3600;

    private final ObjectMapper objectMapper;

    public JwtService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String generate(String login, UserRole role) {
        long now = Instant.now().getEpochSecond();
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", login);
        payload.put("iat", now);
        payload.put("exp", now + TTL_SECONDS);
        payload.put("role", role.name());
        String head = encodeJson(header);
        String body = encodeJson(payload);
        return head + "." + body + "." + sign(head + "." + body);
    }

    public TokenData validate(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3 || !constantEquals(parts[2], sign(parts[0] + "." + parts[1]))) {
                return null;
            }
            Map<String, Object> payload = objectMapper.readValue(decode(parts[1]), new TypeReference<>() {});
            long exp = ((Number) payload.get("exp")).longValue();
            if (Instant.now().getEpochSecond() >= exp) {
                return null;
            }
            return new TokenData((String) payload.get("sub"), UserRole.valueOf((String) payload.get("role")));
        } catch (Exception ex) {
            return null;
        }
    }

    private String encodeJson(Object value) {
        try {
            return encode(objectMapper.writeValueAsBytes(value));
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot build token", ex);
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return encode(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot sign token", ex);
        }
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private byte[] decode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }

    private boolean constantEquals(String left, String right) {
        return MessageDigestHolder.equals(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
    }

    public record TokenData(String login, UserRole role) {
    }

    private static class MessageDigestHolder {
        static boolean equals(byte[] left, byte[] right) {
            return java.security.MessageDigest.isEqual(left, right);
        }
    }
}
