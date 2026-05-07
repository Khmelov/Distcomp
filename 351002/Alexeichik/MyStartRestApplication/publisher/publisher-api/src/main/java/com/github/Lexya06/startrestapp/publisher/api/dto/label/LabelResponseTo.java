package com.github.Lexya06.startrestapp.publisher.api.dto.label;

import com.github.Lexya06.startrestapp.publisher.api.dto.article.ArticleResponseTo;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelResponseTo {
    Long id;
    String name;
    @Builder.Default
    Set<ArticleResponseTo> articles = new HashSet<>();
}
