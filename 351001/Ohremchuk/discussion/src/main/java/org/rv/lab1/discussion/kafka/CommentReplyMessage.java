package org.rv.lab1.discussion.kafka;

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
    public static CommentReplyMessage ok(UUID requestId, Long id, Long storyId, String content, String state) {
        return new CommentReplyMessage(requestId, true, null, id, storyId, content, state);
    }

    public static CommentReplyMessage fail(UUID requestId, String error) {
        return new CommentReplyMessage(requestId, false, error, null, null, null, null);
    }
}
