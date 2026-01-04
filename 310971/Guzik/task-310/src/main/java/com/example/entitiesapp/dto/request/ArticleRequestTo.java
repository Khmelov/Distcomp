package com.example.entitiesapp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleRequestTo {
    @NotNull(message = "writerId is required")
    private Long writerId;

    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;

    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;
}