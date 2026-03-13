package com.example.lab.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewsResponseTo {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    
    public NewsResponseTo(
            @JsonProperty("id") Long id,
            @JsonProperty("userId") Long userId,
            @JsonProperty("title") String title,
            @JsonProperty("content") String content,
            @JsonProperty("created") LocalDateTime created,
            @JsonProperty("modified") LocalDateTime modified) {
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
}
