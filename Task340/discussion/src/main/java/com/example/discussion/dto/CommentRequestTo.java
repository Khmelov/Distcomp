package com.example.discussion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record CommentRequestTo(
        Long id,
        @NotNull Long storyId,
        @NotBlank String content
) {}