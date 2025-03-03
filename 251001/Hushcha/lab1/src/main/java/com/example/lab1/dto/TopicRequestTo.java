package com.example.lab1.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TopicRequestTo(
        Long id,
        @Size(min = 2, max = 64)
        String title,
        String content,
        Long userId
) {
}
