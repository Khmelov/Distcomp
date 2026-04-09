package com.example.lab.discussion.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Table("tbl_post")
public class Post {

    @PrimaryKey
    private Long id;

    @Column("newsId")
    private Long newsId;

    @NotBlank
    @Size(min = 2, max = 2048)
    @Column("content")
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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}