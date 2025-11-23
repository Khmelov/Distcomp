package com.rest.restapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequestToDto(
        @NotBlank(message = "Name is required")
        @Size(max = 32, message = "Name must not exceed 32 characters")
        String name
) {
}
