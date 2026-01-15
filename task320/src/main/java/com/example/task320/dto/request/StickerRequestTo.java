package com.example.task320.dto.request;

import jakarta.validation.constraints.*;

public record StickerRequestTo(
        @NotBlank @Size(min = 2, max = 32) String name
) {}
