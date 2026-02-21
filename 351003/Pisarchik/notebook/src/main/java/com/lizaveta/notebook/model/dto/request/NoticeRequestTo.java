package com.lizaveta.notebook.model.dto.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonRootName("notice")
public record NoticeRequestTo(
        @NotNull(message = "Story id must not be null")
        Long storyId,

        @NotBlank(message = "Content must not be blank")
        @Size(min = 2, max = 2048, message = "Content length must be between 2 and 2048")
        String content) {
}
