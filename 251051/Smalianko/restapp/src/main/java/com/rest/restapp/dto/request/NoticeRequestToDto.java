package com.rest.restapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoticeRequestToDto(
        @NotNull(message = "Issue is required")
        Long issueId,


        @NotBlank(message = "Content is required")
        @Size(max = 2048, message = "Content must not exceed 2048 characters")
        String content
) {
}
