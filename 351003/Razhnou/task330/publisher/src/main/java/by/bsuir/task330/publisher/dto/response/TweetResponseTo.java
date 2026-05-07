package by.bsuir.task330.publisher.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TweetResponseTo(
        Long id,
        @JsonProperty("userId")
        Long userId,
        String title,
        String content,
        String created,
        String modified
) {
}
