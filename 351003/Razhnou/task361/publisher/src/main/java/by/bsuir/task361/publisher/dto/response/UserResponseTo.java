package by.bsuir.task361.publisher.dto.response;

import by.bsuir.task361.publisher.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponseTo(
        Long id,
        String login,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("lastname")
        String lastname,
        UserRole role
) {
}
