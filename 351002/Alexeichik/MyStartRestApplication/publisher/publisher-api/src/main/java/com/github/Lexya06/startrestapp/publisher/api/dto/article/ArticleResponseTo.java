package com.github.Lexya06.startrestapp.publisher.api.dto.article;

import com.github.Lexya06.startrestapp.publisher.api.dto.label.LabelResponseTo;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponseTo {
    Long id;
    Long userId;
    String title;
    String content;
    OffsetDateTime created;
    OffsetDateTime modified;
    @Builder.Default
    List<LabelResponseTo> labels = new ArrayList<>();
}
