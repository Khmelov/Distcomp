package by.bsuir.task350.discussion.kafka.dto;

import java.util.List;

public record ReactionKafkaResponse(
        String requestId,
        boolean success,
        int status,
        Integer errorCode,
        String errorMessage,
        ReactionPayload payload,
        List<ReactionPayload> payloadList
) {
}
