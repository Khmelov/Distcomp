package com.example.common.dto;

import jakarta.validation.constraints.*;

public record MessageRequestTo(
        @NotNull Long articleId,
        @NotBlank @Size(min = 2, max = 2048) String content
) {}
