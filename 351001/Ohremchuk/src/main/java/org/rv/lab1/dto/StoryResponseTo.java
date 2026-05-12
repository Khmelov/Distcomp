package org.rv.lab1.dto;

import java.time.Instant;
import java.util.Set;

public record StoryResponseTo(
        long id,
        long editorId,
        String title,
        String content,
        Set<Long> markerIds,
        Instant createdAt,
        Instant updatedAt
) {
}

