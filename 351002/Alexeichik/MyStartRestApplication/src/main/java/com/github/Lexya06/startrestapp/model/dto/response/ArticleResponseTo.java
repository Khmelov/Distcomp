package com.github.Lexya06.startrestapp.model.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Value
@Builder
public class ArticleResponseTo {
    Long id;
    Long userId;
    String title;
    String content;
    OffsetDateTime created;
    OffsetDateTime modified;
    Set<LabelResponseTo> labels = new HashSet<>();
}
