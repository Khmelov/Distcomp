package com.lizaveta.notebook.model.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Response DTO for Notice entity.
 */
@JsonRootName("notice")
public record NoticeResponseTo(
        Long id,
        Long storyId,
        String content) {
}
