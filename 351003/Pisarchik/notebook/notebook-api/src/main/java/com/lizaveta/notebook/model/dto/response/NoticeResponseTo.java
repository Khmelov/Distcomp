package com.lizaveta.notebook.model.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.lizaveta.notebook.model.NoticeState;

@JsonRootName("notice")
public record NoticeResponseTo(
        Long id,
        Long storyId,
        String content,
        NoticeState state) {
}
