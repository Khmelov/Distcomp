package by.bsuir.distcomp.kafka;

import by.bsuir.distcomp.dto.CommentDto;

public record CommentKafkaRequest(
        String requestId,
        CommentOperation operation,
        Long id,
        CommentDto comment,
        int page,
        int size
) {
}
