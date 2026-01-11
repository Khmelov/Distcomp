package by.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentRequestTo {
    @NotNull 
    private Long storyId;
    
    @NotBlank @Size(min = 2, max = 2048) 
    private String content;

    // Getters and Setters
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}