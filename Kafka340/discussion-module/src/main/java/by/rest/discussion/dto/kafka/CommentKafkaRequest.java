// src/main/java/by/rest/discussion/dto/kafka/CommentKafkaRequest.java
package by.rest.discussion.dto.kafka;

import java.io.Serializable;

public class CommentKafkaRequest implements Serializable {
    private String commentId;
    private String content;
    private Long storyId;
    private String author;
    private Long timestamp;
    
    // Конструкторы
    public CommentKafkaRequest() {}
    
    public CommentKafkaRequest(String commentId, String content, Long storyId, String author) {
        this.commentId = commentId;
        this.content = content;
        this.storyId = storyId;
        this.author = author;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Геттеры и сеттеры
    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}