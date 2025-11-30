package com.rest.restapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IssueRequestToDto(
        @NotNull(message = "Author is required")
        Long authorId,
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 64, message = "Title length is not valid")
        String title,
        @NotBlank(message = "Content is required")
        @Size(max = 2048, message = "Content must not exceed 2048 characters")
        String content
) {
}
