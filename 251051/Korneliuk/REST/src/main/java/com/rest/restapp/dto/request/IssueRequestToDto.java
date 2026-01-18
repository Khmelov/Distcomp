package com.rest.restapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IssueRequestToDto(
        @NotNull(message = "userId is required")
        Long userId,
        @NotBlank(message = "title is required")
        @Size(min = 2, max = 64, message = "Title (2...64 chars)")
        String title,
        @NotBlank(message = "Content is required")
        @Size(min = 4, max = 2048, message = "Content (4...2048 chars)")
        String content
) {
}
