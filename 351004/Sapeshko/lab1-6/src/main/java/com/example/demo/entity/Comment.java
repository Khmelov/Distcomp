package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.springframework.context.annotation.Profile;
@Entity
@Table(name = "tbl_comment", schema = "distcomp")
@Profile("docker")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "news_id", nullable = false)
    @JsonIgnore
    private News news;

    @Column(nullable = false, length = 2048)
    @Size(min = 2, max = 2048)
    private String content;

    public Comment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public News getNews() { return news; }
    public void setNews(News news) { this.news = news; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getNewsId() {
        return news != null ? news.getId() : null;
    }
}