package com.example.task320.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_notice")
public class NoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "news_id", nullable = false)
    private NewsEntity news;

    @Column(nullable = false, length = 2048)
    private String content;

    public Long getId() { return id; }
    public NewsEntity getNews() { return news; }
    public String getContent() { return content; }

    public void setId(Long id) { this.id = id; }
    public void setNews(NewsEntity news) { this.news = news; }
    public void setContent(String content) { this.content = content; }
}
