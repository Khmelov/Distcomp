package com.rest.restapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestToDto(
        @NotBlank(message = "login is required")
        @Size(min = 3, max = 64, message = "Login (2...64 chars)")
        String login,
        @NotBlank(message = "password is required")
        @Size(min = 8, max = 128, message = "Password (8...128 chars)")
        String password,
        @NotBlank(message = "firstname is required")
        @Size(min = 2, max = 64, message = "Firstname (2...64 chars)")
        String firstname,
        @NotBlank(message = "lastname is required")
        @Size(min = 2, max = 64, message = "Lastname (2...64 chars)")
        String lastname
) {
}
