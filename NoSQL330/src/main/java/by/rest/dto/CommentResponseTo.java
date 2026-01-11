package by.rest.dto;

public class CommentResponseTo {
    private Long id;
    private Long storyId;
    private String content;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}