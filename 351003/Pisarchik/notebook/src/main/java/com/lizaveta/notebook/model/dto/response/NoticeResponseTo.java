package com.lizaveta.notebook.model.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("notice")
public record NoticeResponseTo(
        Long id,
        Long storyId,
        String content) {
}
