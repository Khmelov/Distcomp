package com.example.Task310.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StoryResponseTo(
        Long id,
        Long editorId,
        String title,
        String content,
        LocalDateTime created,
        LocalDateTime modified,
        List<MarkerResponseTo> markers
) {}