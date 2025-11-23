package com.rest.restapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorRequestToDto(
        @NotBlank(message = "Login is required")
        @Size(min = 3, max = 64, message = "Login must not exceed 64 characters")
        String login,
        @NotBlank(message = "Password is required")
        @Size(max = 128, message = "Password must not exceed 128 characters")
        String password,
        @NotBlank(message = "Firstname is required")
        @Size(max = 64, message = "Firstname must not exceed 64 characters")
        String firstname,
        @NotBlank(message = "Lastname is required")
        @Size(max = 64, message = "Lastname must not exceed 64 characters")
        String lastname
) {
}
