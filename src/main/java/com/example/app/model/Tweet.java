package com.example.app.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class Tweet extends BaseEntity {
    @NotNull
    private Long authorId;          // ID автора (было storyId)
    @NotBlank @Size(min = 2, max = 64)
    private String title;           // Заголовок твита (новое поле)
    @NotBlank @Size(min = 2, max = 2048)
    private String content;         // Содержание твита
    private Instant created = Instant.now();
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