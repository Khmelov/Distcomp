package by.rest.publisher.dto.kafka;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ModerationResult implements Serializable {
    private String commentId;
    private Long storyId;
    private String status;  // APPROVE, DECLINE
    private String reason;
    private LocalDateTime moderatedAt = LocalDateTime.now();
    private String moderator = "auto-moderation";
    
    // Конструкторы
    public ModerationResult() {}
    
    public ModerationResult(String commentId, Long storyId, String status, String reason) {
        this.commentId = commentId;
        this.storyId = storyId;
        this.status = status;
        this.reason = reason;
        this.moderatedAt = LocalDateTime.now();
    }
    
    // Геттеры и сеттеры
    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }
    
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public LocalDateTime getModeratedAt() { return moderatedAt; }
    public void setModeratedAt(LocalDateTime moderatedAt) { this.moderatedAt = moderatedAt; }
    
    public String getModerator() { return moderator; }
    public void setModerator(String moderator) { this.moderator = moderator; }
}