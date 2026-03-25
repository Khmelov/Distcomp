package com.github.Lexya06.startrestapp.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.Lexya06.startrestapp.service.mapper.config.deserializer.Label.LabelRequestToListFromStringList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @JsonDeserialize(using = LabelRequestToListFromStringList.class)
    List<LabelRequestTo> labels = new ArrayList<>();
}
