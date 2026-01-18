package com.rest.restapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequestTo(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 32, message = "Name length is not valid")
        String name
) {
}
