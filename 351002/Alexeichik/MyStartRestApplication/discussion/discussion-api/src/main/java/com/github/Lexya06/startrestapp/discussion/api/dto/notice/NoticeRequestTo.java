package com.github.Lexya06.startrestapp.discussion.api.dto.notice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
public class NoticeRequestTo {
    String country;

    @NotNull
    Long articleId;

    @NotBlank
    @Size(min = 2, max = 2048)
    String content;

    @Builder
    @JsonCreator
    public NoticeRequestTo(
            @JsonProperty("country") String country,
            @JsonProperty("articleId") Long articleId,
            @JsonProperty("content") String content
    ) {

        this.country = (country == null) ? "belarus" : country;
        this.articleId = articleId;
        this.content = content;
    }
}
