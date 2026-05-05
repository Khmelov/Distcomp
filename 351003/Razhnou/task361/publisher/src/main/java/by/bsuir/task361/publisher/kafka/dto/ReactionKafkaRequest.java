package by.bsuir.task361.publisher.kafka.dto;

public record ReactionKafkaRequest(
        String requestId,
        ReactionOperation operation,
        Long tweetIdKey,
        Long reactionId,
        ReactionPayload payload
) {
}
