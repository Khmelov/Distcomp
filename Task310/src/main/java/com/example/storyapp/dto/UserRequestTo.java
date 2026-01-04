package com.example.storyapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestTo(
        Long id,
        @NotBlank @Size(min = 2, max = 32) String login,
        @NotBlank @Size(min = 6, max = 128) String password,
        String firstname,
        String lastname
) {}