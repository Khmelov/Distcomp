package by.bsuir.task340.discussion.kafka.dto;

public record ReactionKafkaRequest(
        String requestId,
        ReactionOperation operation,
        Long tweetIdKey,
        Long reactionId,
        ReactionPayload payload
) {
}
