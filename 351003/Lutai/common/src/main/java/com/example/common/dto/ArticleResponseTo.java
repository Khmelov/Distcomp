package com.example.common.dto;

import java.util.List;
import java.time.LocalDateTime;

public record ArticleResponseTo(
        Long id,
        Long writerId,
        String title,
        String content,
        LocalDateTime created,
        LocalDateTime modified,
        List<Long> markerIds,
        List<MessageResponseTo> messages
) {}
