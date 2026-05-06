package com.github.Lexya06.startrestapp.publisher.api.dto.article;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.Lexya06.startrestapp.publisher.api.deserializer.Label.LabelRequestToListFromStringList;
import com.github.Lexya06.startrestapp.publisher.api.dto.label.LabelRequestTo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

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

    @JsonDeserialize(using = LabelRequestToListFromStringList.class)
    List<LabelRequestTo> labels = new ArrayList<>();
}
