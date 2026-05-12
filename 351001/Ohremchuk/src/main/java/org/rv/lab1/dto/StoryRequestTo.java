package org.rv.lab1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record StoryRequestTo(
        @NotNull Long editorId,
        @NotBlank @Size(min = 2, max = 64) String title,
        @NotBlank @Size(min = 2, max = 2048) String content,
        Set<Long> markerIds
) {
}

