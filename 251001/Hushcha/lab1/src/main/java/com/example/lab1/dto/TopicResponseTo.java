package com.example.lab1.dto;

import java.time.LocalDateTime;

public record TopicResponseTo(
        Long id,
        String title,
        String content,
        Long userId,
        LocalDateTime created,
        LocalDateTime modified
) {
}
