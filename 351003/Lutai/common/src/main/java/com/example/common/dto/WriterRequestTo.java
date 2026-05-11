package com.example.common.dto;

import com.example.common.dto.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WriterRequestTo(
        @NotBlank @Size(min = 2, max = 64) String login,
        @NotBlank @Size(min = 8, max = 128) String password,
        @NotBlank @Size(min = 2, max = 64) String firstname,
        @NotBlank @Size(min = 2, max = 64) String lastname,
        Role role
) {}