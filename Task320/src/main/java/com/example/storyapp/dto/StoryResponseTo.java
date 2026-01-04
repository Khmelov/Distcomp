package com.example.storyapp.dto;

import java.time.Instant;
import java.util.List;

public record StoryResponseTo(
        Long id,
        Long userId,
        String title,
        String content,
        Instant created,
        Instant modified,
        List<String> labels  // ← возвращаем имена меток
) {}