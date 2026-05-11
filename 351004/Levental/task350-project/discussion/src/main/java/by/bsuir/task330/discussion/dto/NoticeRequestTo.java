package by.bsuir.task330.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoticeRequestTo(
        Long id,
        @NotNull Long articleId,
        @NotBlank @Size(min = 2, max = 2048) String content
) {}
