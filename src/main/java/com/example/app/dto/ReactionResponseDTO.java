package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReactionResponseDTO(
        Long id,
        Long tweetId,
        @NotBlank @Size(min = 2, max = 2048) String content
) {}