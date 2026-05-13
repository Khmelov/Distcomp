package org.rv.lab1.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommentReplyMessage(
        UUID requestId,
        boolean success,
        String errorMessage,
        Long id,
        Long storyId,
        String content,
        String state
) {
}
