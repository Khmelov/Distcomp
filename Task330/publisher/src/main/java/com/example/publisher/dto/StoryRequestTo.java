package com.example.publisher.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record StoryRequestTo(
        Long id,
        @NotNull Long userId,
        @NotBlank @Size(min = 2, max = 255) String title,
        @NotBlank String content,
        List<@NotBlank @Size(min = 2, max = 64) String> labels  // ← поддержка меток
) {}