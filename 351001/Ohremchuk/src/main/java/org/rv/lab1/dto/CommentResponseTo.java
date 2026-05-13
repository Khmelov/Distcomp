package org.rv.lab1.dto;

public record CommentResponseTo(
        long id,
        long storyId,
        String content,
        CommentState state
) {
}

