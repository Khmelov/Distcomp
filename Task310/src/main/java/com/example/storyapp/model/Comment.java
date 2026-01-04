package com.example.storyapp.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public class Comment extends BaseEntity {
    @NotNull
    private Long storyId;
    @NotBlank
    private String content;
    private Instant created = Instant.now();

    public Comment() {}
    public Comment(Long id, Long storyId, String content) {
        this.id = id;
        this.storyId = storyId;
        this.content = content;
    }

    //геттеры и сеттеры
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreated() { return created; }
    public void setCreated(Instant created) { this.created = created; }
}