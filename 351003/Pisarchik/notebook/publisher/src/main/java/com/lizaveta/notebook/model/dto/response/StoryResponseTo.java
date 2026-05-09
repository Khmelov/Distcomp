package com.lizaveta.notebook.model.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.time.LocalDateTime;
import java.util.Set;

@JsonRootName("story")
public record StoryResponseTo(
        Long id,
        Long writerId,
        String title,
        String content,
        LocalDateTime created,
        LocalDateTime modified,
        Set<Long> markerIds) {
}
