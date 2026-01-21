package com.example.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(name = "tbl_tweet")
public class Tweet extends BaseEntity {
    @NotNull
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    
    @NotBlank @Size(min = 2, max = 64)
    @Column(name = "title", nullable = false, length = 64)
    private String title;
    
    @NotBlank @Size(min = 2, max = 2048)
    @Column(name = "content", nullable = false, length = 2048)
    private String content;
    
    @Column(name = "created", nullable = false)
    private Instant created = Instant.now();
    
    @Column(name = "modified", nullable = false)
    private Instant modified = Instant.now();

    //конструктор
    public Tweet() {}
    public Tweet(Long id, Long authorId, String title, String content) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
    }
    public Tweet(Long id, Long authorId, String title, String content, Instant created, Instant modified) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
    }

    //геттеры и сеттеры
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreated() { return created; }
    public void setCreated(Instant created) { this.created = created; }

    public Instant getModified() { return modified; }
    public void setModified(Instant modified) { this.modified = modified; }
}