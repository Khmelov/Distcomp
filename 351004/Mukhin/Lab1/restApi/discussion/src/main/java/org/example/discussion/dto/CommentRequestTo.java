package org.example.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentRequestTo {

    private Long id;

    @NotNull(message = "Article ID cannot be null")
    private Long articleId;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 2, max = 2048)
    private String content;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}