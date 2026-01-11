package com.publisher.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CommentEvent {
    private UUID commentId;
    private UUID storyId;
    private String content;
    private String author;
    private CommentStatus status;
    private Long timestamp;
    
    public enum CommentStatus {
        PENDING, APPROVE, DECLINE
    }
}