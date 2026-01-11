package by.rest.publisher.dto.kafka;

import java.util.UUID;

public class CommentKafkaResponse {
    private UUID commentId;
    private String status;
    private String moderatedDate;
    
    public CommentKafkaResponse() {}
    
    public CommentKafkaResponse(UUID commentId, String status) {
        this.commentId = commentId;
        this.status = status;
        this.moderatedDate = java.time.Instant.now().toString();
    }
    
    // Геттеры и сеттеры
    public UUID getCommentId() { return commentId; }
    public void setCommentId(UUID commentId) { this.commentId = commentId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getModeratedDate() { return moderatedDate; }
    public void setModeratedDate(String moderatedDate) { this.moderatedDate = moderatedDate; }
    
    @Override
    public String toString() {
        return "CommentKafkaResponse{" +
                "commentId=" + commentId +
                ", status='" + status + '\'' +
                '}';
    }
}