package com.example.task310.dto.request;

import jakarta.validation.constraints.*;

public record WriterRequestTo(
        @NotBlank @Size(min = 2, max = 64) String login,
        @NotBlank @Size(min = 8, max = 128) String password,
        @NotBlank @Size(min = 2, max = 64) String firstname,
        @NotBlank @Size(min = 2, max = 64) String lastname
) {}
