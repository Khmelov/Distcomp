// src/main/java/by/rest/discussion/domain/Comment.java
package by.rest.discussion.domain;

public class Comment {
    
    private Long id;
    private Long storyId;
    private String content;
    
    // Конструкторы
    public Comment() {}
    
    public Comment(Long id, Long storyId, String content) {
        this.id = id;
        this.storyId = storyId;
        this.content = content;
    }
    
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}