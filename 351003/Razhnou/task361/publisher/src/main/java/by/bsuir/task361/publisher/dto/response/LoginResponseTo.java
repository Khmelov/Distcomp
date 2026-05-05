package by.bsuir.task361.publisher.dto.response;

import by.bsuir.task361.publisher.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseTo(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("token_type")
        String tokenType,
        UserRole role,
        @JsonProperty("expires_in")
        long expiresIn
) {
}
