package com.rest.restapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record IssueRequestToDto(
        @NotNull(message = "User is required")
        Long userId,
        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 64, message = "Title length is not valid")
        String title,
        @NotBlank(message = "Content is required")
        @Size(min = 4, max = 2048, message = "Content length is not valid")
        String content,
        List<@NotBlank(message = "Tag cannot be blank") String> tags
) {
}
