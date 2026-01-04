package com.example.storyapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoryRequestTo(
        Long id,
        @NotNull Long userId,
        @NotBlank @Size(min = 1, max = 255) String title,
        @NotBlank String content
) {}