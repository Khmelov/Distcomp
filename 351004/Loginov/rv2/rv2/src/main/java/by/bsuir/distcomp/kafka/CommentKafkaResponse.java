package by.bsuir.distcomp.kafka;

import by.bsuir.distcomp.dto.CommentDto;
import java.util.List;

public record CommentKafkaResponse(
        String requestId,
        boolean success,
        String errorCode,
        String errorMessage,
        CommentDto comment,
        List<CommentDto> comments
) {
    public static CommentKafkaResponse ok(String requestId, CommentDto comment) {
        return new CommentKafkaResponse(requestId, true, null, null, comment, null);
    }

    public static CommentKafkaResponse ok(String requestId, List<CommentDto> comments) {
        return new CommentKafkaResponse(requestId, true, null, null, null, comments);
    }

    public static CommentKafkaResponse empty(String requestId) {
        return new CommentKafkaResponse(requestId, true, null, null, null, null);
    }

    public static CommentKafkaResponse error(String requestId, String errorCode, String errorMessage) {
        return new CommentKafkaResponse(requestId, false, errorCode, errorMessage, null, null);
    }
}
