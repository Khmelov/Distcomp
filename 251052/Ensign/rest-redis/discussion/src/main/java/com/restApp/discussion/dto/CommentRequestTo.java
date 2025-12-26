package com.restApp.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequestTo(
                Long id,

                @NotNull Long newsId,

                @NotBlank @Size(min = 2, max = 2048) String content,

                // We might need country in request or infer it. Adding it for now as it is the
                // partition key.
                // If not provided, we should probably default it or throw error.
                String country) {
}
