package com.example.storyapp.dto;

import java.time.Instant;

public record StoryResponseTo(
        Long id,
        Long userId,
        String title,
        String content,
        Instant created,
        Instant modified
) {}