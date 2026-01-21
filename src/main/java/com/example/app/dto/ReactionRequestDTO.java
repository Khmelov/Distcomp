package com.example.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReactionRequestDTO(
        Long id,
        @NotNull Long tweetId,
        @NotBlank @Size(min = 2, max = 2048) String content
) {}