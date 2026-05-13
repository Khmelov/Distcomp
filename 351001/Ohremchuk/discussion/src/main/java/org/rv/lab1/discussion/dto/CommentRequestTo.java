package org.rv.lab1.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequestTo(
        @NotNull Long storyId,
        @NotBlank @Size(min = 2, max = 2048) String content
) {
}

