package org.rv.lab1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MarkerRequestTo(
        @NotBlank @Size(min = 2, max = 64) String name
) {
}

