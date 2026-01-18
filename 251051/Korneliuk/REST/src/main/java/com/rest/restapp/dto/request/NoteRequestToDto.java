package com.rest.restapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoteRequestToDto(
        @NotNull(message = "issueId is required")
        Long issueId,
        @NotBlank(message = "content is required")
        @Size(min = 2, max = 2048, message = "content (2...2048 chars)")
        String content
)
{
}
