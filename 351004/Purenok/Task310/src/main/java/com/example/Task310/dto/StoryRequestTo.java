package com.example.Task310.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StoryRequestTo(
        @NotNull Long editorId,
        @NotBlank @Size(min = 2, max = 64) String title,
        @NotBlank @Size(min = 4, max = 2048) String content
) {}