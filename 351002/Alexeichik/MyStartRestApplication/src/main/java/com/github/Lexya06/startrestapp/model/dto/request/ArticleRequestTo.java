package com.github.Lexya06.startrestapp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;

@Builder
@Value
public class ArticleRequestTo {
    @NotNull
    Long userId;

    @NotBlank
    @Size(min = 2, max = 64)
    String title;

    @NotBlank
    @Size(min = 4, max = 2048)
    String content;

    Set<Long> labelIds = new HashSet<>();
}
