package org.rv.lab1.discussion.dto;

import org.rv.lab1.discussion.domain.CommentState;

public record CommentResponseTo(
        long id,
        long storyId,
        String content,
        CommentState state
) {
}

