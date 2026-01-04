package com.example.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LabelRequestTo(
        Long id,
        @NotBlank @Size(min = 2, max = 64) String name
) {}