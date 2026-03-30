package org.example.newsapi.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequestTo {
    @NotNull
    private Long newsId;

    @Size(min = 2, max = 2048)
    private String content;
}