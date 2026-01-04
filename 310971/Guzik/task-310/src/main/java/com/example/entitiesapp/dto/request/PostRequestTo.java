package com.example.entitiesapp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestTo {
    @NotNull(message = "articleId is required")
    private Long articleId;

    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    private String content;
}