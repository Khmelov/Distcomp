package com.example.app.dto;

import java.time.Instant;

public record TweetResponseDTO(
        Long id,
        Long authorId,
        String title,
        String content,
        Instant created,
        Instant modified
) {}