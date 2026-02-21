package com.lizaveta.notebook.model.dto.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

@JsonRootName("story")
public record StoryRequestTo(
        @NotNull(message = "Writer id must not be null")
        Long writerId,

        @NotBlank(message = "Title must not be blank")
        @Size(min = 2, max = 64, message = "Title length must be between 2 and 64")
        String title,

        @NotBlank(message = "Content must not be blank")
        @Size(min = 4, max = 2048, message = "Content length must be between 4 and 2048")
        String content,

        Set<Long> markerIds,

        Set<String> markerNames) {
}
