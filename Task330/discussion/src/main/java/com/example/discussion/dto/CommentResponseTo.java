package com.example.discussion.dto;

import java.time.Instant;

public record CommentResponseTo(
        Long id,
        Long storyId,
        String content,
        Instant created
) {}