package com.example.common.dto;

import java.util.List;

import jakarta.validation.constraints.*;

public record ArticleRequestTo(
        @NotNull Long writerId,
        @NotBlank @Size(min = 2, max = 64) String title,
        @NotBlank @Size(min = 4, max = 2048) String content,
        List<Long> markerIds
) {
    public ArticleRequestTo {
        if (markerIds == null) markerIds = java.util.Collections.emptyList();
    }
}