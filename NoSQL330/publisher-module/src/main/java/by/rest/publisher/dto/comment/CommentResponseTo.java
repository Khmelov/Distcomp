package by.rest.publisher.dto.comment;

public class CommentResponseTo {
    
    private Long id;
    private Long storyId;
    private String content;
    
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}