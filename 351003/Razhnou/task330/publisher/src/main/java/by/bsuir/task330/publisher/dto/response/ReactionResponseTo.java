package by.bsuir.task330.publisher.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReactionResponseTo(
        Long id,
        @JsonProperty("tweetId")
        Long tweetId,
        String content
) {
}
