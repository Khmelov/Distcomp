package com.github.Lexya06.startrestapp.model.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NoticeResponseTo {
    Long id;
    Long articleId;
    String content;
}
