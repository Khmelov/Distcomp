package com.example.restApi.model;

public class Comment extends BaseEntity {
    private Long articleId;
    private String content;

    public Comment() {
        super();
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
