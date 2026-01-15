package com.example.task320.dto.request;

import jakarta.validation.constraints.*;

public record NoticeRequestTo(
        @NotNull Long newsId,
        @NotBlank @Size(min = 2, max = 2048) String content
) {}
