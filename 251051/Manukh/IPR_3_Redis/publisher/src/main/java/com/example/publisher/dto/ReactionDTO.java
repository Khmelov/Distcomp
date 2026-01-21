package com.example.publisher.dto;

import java.time.LocalDateTime;

public class ReactionDTO {
    private String id;
    private Long storyId;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    private String state; // PENDING, APPROVE, DECLINE

    // Конструкторы
    public ReactionDTO() {}

    public ReactionDTO(String id, Long storyId, String content) {
        this.id = id;
        this.storyId = storyId;
        this.content = content;
        this.state = "PENDING";
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.modified = LocalDateTime.now();
    }

    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }

    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }

    public String getState() { return state; }
    public void setState(String state) {
        this.state = state;
        this.modified = LocalDateTime.now();
    }
}