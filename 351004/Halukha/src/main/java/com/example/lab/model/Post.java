package com.example.lab.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tbl_post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "newsId")
    private Long newsId;

    @NotBlank
    @Size(min = 2, max = 2048)
    @Column(name = "content")
    private String content;

    public Post() {
    }

    public Post(Long id, Long newsId, String content) {
        this.id = id;
        this.newsId = newsId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Long getNewsId() {
        return newsId;
    }

    public String getContent() {
        return content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
