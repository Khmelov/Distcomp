package by.bsuir.task350.discussion.dto.response;

import by.bsuir.task350.discussion.dto.ReactionState;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ReactionResponseTo(
        Long id,
        @JsonProperty("tweetId")
        Long tweetId,
        String content,
        ReactionState state
) {
}
