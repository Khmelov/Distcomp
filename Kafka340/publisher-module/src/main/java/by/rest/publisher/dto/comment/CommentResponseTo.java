package by.rest.publisher.dto.comment;

import java.util.UUID;

public class CommentResponseTo {
    
    private UUID id;
    private Long storyId;
    private String content;
    private String author;
    private String status; 
    
    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}