package com.example.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestTo(
        Long id,
        @NotBlank @Size(min = 2, max = 32) String login,
        @NotBlank @Size(min = 6, max = 128) String password,
        @Size(max = 64) String firstname,
        @Size(max = 64) String lastname
) {}