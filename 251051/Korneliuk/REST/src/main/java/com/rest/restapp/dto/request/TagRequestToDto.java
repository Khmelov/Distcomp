package com.rest.restapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequestToDto(
        @NotBlank(message = "name is required")
        @Size(min = 2, max = 32, message = "name (2...32 chars)")
        String name
) {
}
