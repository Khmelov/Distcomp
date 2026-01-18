package com.rest.restapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestTo(
        @NotBlank(message = "Login is required")
        @Size(min = 3, max = 64, message = "Login length is not valid")
        String login,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "Password length is not valid")
        String password,
        @NotBlank(message = "Firstname is required")
        @Size(min = 2, max = 64, message = "Firstname length is not valid")
        String firstname,
        @NotBlank(message = "Lastname is required")
        @Size(min = 2, max = 64, message = "Lastname length is not valid")
        String lastname
) {
}
