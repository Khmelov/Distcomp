package com.lizaveta.notebook.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestTo(
        @NotBlank(message = "Login must not be blank")
        String login,
        @NotBlank(message = "Password must not be blank")
        String password) {
}
