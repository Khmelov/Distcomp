package by.rest.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentRequestTo {
    
    @NotNull(message = "Story ID is required")
    private Long storyId;
    
    @NotBlank(message = "Content is required")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;
    
    // Геттеры и сеттеры
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}