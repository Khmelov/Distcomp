package com.rest.restapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoticeRequestToDto(
        @NotNull(message = "Issue is required")
        Long issueId,
        @NotBlank(message = "Content is required")
        @Size(min = 4, max = 2048, message = "Content length is not valid")
        String content
) {
}
