package com.example.restApi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentResponseTo {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("articleId")
    private Long articleId;

    @JsonProperty("content")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
