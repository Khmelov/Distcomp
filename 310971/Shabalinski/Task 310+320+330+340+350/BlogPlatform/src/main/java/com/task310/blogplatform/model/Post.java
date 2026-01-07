package com.task310.blogplatform.model;

import jakarta.persistence.*;

@jakarta.persistence.Entity
@Table(name = "tbl_post", schema = "distcomp")
public class Post extends Entity {
    @Column(name = "article_id", nullable = false, insertable = false, updatable = false)
    private Long articleId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    public Long getArticleId() {
        if (articleId == null && article != null) {
            articleId = article.getId();
        }
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

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
        if (article != null) {
            this.articleId = article.getId();
        }
    }
}

