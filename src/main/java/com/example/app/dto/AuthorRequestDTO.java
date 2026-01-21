package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorRequestDTO(
        Long id,
        @NotBlank @Size(min = 2, max = 32) String login,
        @NotBlank @Size(min = 6, max = 128) String password,
        @Size(min = 1, max = 64) String firstname,
        @Size(min = 1, max = 64) String lastname
) {}