package com.example.Task310.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MarkerRequestTo(
        @NotBlank @Size(min = 2, max = 32) String name
) {}