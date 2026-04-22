package com.example.common.dto;

import jakarta.validation.constraints.*;

public record MessageRequestTo(
        Long id,
        @NotNull Long articleId,
        @NotBlank @Size(min = 2, max = 2048) String content
) {}
