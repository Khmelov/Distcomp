// Mark.java
package com.example.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Mark extends BaseEntity {
    private String name;
    private LocalDateTime created;
    private LocalDateTime modified;
    private Set<Long> storyIds = new HashSet<>();

    // Constructors
    public Mark() {}

    public Mark(Long id, String name) {
        this.id = id;
        this.name = name;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.modified = LocalDateTime.now();
    }

    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }

    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }

    public Set<Long> getStoryIds() { return storyIds; }
    public void setStoryIds(Set<Long> storyIds) { this.storyIds = storyIds; }

    public void addStory(Long storyId) {
        this.storyIds.add(storyId);
    }

    public void removeStory(Long storyId) {
        this.storyIds.remove(storyId);
    }
}