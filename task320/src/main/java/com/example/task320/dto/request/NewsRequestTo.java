package com.example.task320.dto.request;

import jakarta.validation.constraints.*;

public record NewsRequestTo(
        @NotNull Long writerId,
        @NotBlank @Size(min = 2, max = 64) String title,
        @NotBlank @Size(min = 4, max = 2048) String content
) {}
