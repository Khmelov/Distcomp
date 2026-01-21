package com.example.publisher.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public class MarkResponseTo {
    private Long id;
    private String name;
    private LocalDateTime created;
    private LocalDateTime modified;
    private Set<Long> storyIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public Set<Long> getStoryIds() {
        return storyIds;
    }

    public void setStoryIds(Set<Long> storyIds) {
        this.storyIds = storyIds;
    }
}