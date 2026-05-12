package by.bsuir.task361.publisher.kafka.dto;

import by.bsuir.task361.publisher.dto.ReactionState;

public record ReactionPayload(
        Long id,
        Long tweetId,
        String content,
        ReactionState state
) {
}
