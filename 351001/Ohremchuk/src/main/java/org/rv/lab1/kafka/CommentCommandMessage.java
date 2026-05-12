package org.rv.lab1.kafka;

import java.util.UUID;

public record CommentCommandMessage(
        UUID requestId,
        String operation,
        Long storyId,
        Long commentId,
        String content
) {
}
