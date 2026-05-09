package by.bsuir.task340.publisher.kafka.dto;

import by.bsuir.task340.publisher.dto.ReactionState;

public record ReactionPayload(
        Long id,
        Long tweetId,
        String content,
        ReactionState state
) {
}
