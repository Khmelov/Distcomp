package com.github.Lexya06.startrestapp.discussion.api.dto.notice;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Value
@Builder
@Jacksonized
public class NoticeKeyDto {
    String country;
    Long articleId;
    Long id;

    @Override
    public String toString() {
        return String.format("%s_%d_%d", country, articleId, id);
    }
}
