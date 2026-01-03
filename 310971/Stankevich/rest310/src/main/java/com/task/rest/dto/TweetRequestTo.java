package com.task.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TweetRequestTo {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("authorId")
    @NotNull(message = "Author ID is required")
    private Long authorId;

    @JsonProperty("title")
    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;

    @JsonProperty("content")
    @NotBlank(message = "Content is required")
    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    private String content;
}