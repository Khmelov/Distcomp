package by.bsuir.task340.discussion.kafka.dto;

import by.bsuir.task340.discussion.dto.ReactionState;

public record ReactionPayload(
        Long id,
        Long tweetId,
        String content,
        ReactionState state
) {
}
