// ReactionRequestTo.java
package com.example.publisher.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReactionRequestTo {
    @NotNull(message = "Story ID cannot be null")
    private Long storyId;

    @NotBlank(message = "Content cannot be empty")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;

    // Getters and Setters
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}