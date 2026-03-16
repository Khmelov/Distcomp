package com.example.Task310.dto;

import java.time.LocalDateTime;

public record StoryResponseTo(
        Long id,
        Long editorId,
        String title,
        String content,
        LocalDateTime created,
        LocalDateTime modified
) {}