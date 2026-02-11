package com.github.Lexya06.startrestapp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NoticeRequestTo {
    @NotNull
    Long articleId;

    @NotBlank
    @Size(min = 2, max = 2048)
    String content;
}
