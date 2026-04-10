package com.example.common.dto;

import jakarta.validation.constraints.*;

public record MarkerRequestTo(
        @NotBlank @Size(min = 2, max = 32) String name
) {}
