package com.github.Lexya06.startrestapp.model.dto.response;

import lombok.Builder;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;

@Value
@Builder
public class LabelResponseTo {
    Long id;
    String name;
    Set<ArticleResponseTo> articles = new HashSet<>();
}
