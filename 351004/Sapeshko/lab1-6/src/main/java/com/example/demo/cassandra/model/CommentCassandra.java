package com.example.demo.cassandra.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("comments")
public class CommentCassandra {
    @PrimaryKey
    private Long id;

    @Column("news_id")
    private Long newsId;

    @Column("content")
    private String content;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}