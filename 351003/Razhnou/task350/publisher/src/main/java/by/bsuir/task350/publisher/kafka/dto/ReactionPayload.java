package by.bsuir.task350.publisher.kafka.dto;

import by.bsuir.task350.publisher.dto.ReactionState;

public record ReactionPayload(
        Long id,
        Long tweetId,
        String content,
        ReactionState state
) {
}
