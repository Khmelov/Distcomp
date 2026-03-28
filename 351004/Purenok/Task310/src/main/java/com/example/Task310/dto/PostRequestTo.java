package com.example.Task310.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostRequestTo(
        @NotNull Long storyId,
        @NotBlank @Size(min = 2, max = 2048) String content
) {}