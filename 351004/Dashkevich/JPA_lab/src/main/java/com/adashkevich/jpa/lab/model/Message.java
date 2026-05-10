package com.adashkevich.jpa.lab.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_message")
public class Message extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Column(nullable = false, length = 2048)
    private String content;

    public News getNews() { return news; }
    public void setNews(News news) { this.news = news; }

    public Long getNewsId() { return news == null ? null : news.getId(); }
    public void setNewsId(Long ignored) { }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
