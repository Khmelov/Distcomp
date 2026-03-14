package com.example.restApi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_comment")
public class Comment extends BaseEntity {

    @Column(name = "text", length = 2048)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    public Comment() {
        super();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}
