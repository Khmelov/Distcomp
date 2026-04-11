package by.bsuir.task340.publisher.kafka.dto;

public record ReactionKafkaRequest(
        String requestId,
        ReactionOperation operation,
        Long tweetIdKey,
        Long reactionId,
        ReactionPayload payload
) {
}
