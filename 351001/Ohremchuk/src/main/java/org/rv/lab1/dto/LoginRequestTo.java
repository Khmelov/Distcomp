package org.rv.lab1.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestTo(
        @NotBlank String login,
        @NotBlank String password
) {
}
