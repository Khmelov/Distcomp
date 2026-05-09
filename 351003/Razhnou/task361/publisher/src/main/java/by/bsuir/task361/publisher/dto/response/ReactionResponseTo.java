package by.bsuir.task361.publisher.dto.response;

import by.bsuir.task361.publisher.dto.ReactionState;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ReactionResponseTo(
        Long id,
        @JsonProperty("tweetId")
        Long tweetId,
        String content,
        ReactionState state
) {
}
