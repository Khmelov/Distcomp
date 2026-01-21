package com.example.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequestDTO(
        Long id,
        @NotBlank @Size(min = 2, max = 32) String name
) {}