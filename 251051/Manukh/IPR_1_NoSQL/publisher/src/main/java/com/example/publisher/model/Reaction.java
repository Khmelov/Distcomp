// Reaction.java
package com.example.publisher.model;

import java.time.LocalDateTime;

public class Reaction extends BaseEntity {
    private Long storyId;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;

    // Constructors
    public Reaction() {}

    public Reaction(Long id, Long storyId, String content) {
        this.id = id;
        this.storyId = storyId;
        this.content = content;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) {
        this.storyId = storyId;
        this.modified = LocalDateTime.now();
    }

    public String getContent() { return content; }
    public void setContent(String content) {
        this.content = content;
        this.modified = LocalDateTime.now();
    }

    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }

    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
}