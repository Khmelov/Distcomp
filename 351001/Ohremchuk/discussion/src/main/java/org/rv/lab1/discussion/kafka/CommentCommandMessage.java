package org.rv.lab1.discussion.kafka;

import java.util.UUID;

/**
 * Command sent by publisher to {@code InTopic} (partition key = storyId).
 */
public record CommentCommandMessage(
        UUID requestId,
        String operation,
        Long storyId,
        Long commentId,
        String content
) {
}
