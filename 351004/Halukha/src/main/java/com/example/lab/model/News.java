package com.example.lab.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userId")
    private Long userId;

    @NotBlank
    @Size(min = 2, max = 64)
    @Column(name = "title")
    private String title;

    @NotBlank
    @Size(min = 4, max = 2048)
    @Column(name = "content")
    private String content;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Column(name = "created")
    private LocalDateTime created;

    @DateTimeFormat(iso = ISO.DATE_TIME)
    @Column(name = "modified")
    private LocalDateTime modified;

    public News() {
    }

    public News(Long id, Long userId, String title, String content, LocalDateTime created, LocalDateTime modified) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }
}
