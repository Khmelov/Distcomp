package by.bsuir.task361.discussion.kafka.dto;

import by.bsuir.task361.discussion.dto.ReactionState;

public record ReactionPayload(
        Long id,
        Long tweetId,
        String content,
        ReactionState state
) {
}
