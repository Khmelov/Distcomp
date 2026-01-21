package com.example.discussion.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("tbl_reaction")
public class Reaction {

    @PrimaryKey
    private String id;

    private Long storyId;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;

    public Reaction() {
        this.id = UUID.randomUUID().toString();
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
}