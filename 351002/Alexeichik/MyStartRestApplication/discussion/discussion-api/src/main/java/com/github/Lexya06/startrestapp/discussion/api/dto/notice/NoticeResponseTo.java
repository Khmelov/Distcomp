package com.github.Lexya06.startrestapp.discussion.api.dto.notice;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
public class NoticeResponseTo {
    String country;
    Long articleId;
    Long id;
    String content;
}
