package com.example.app.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaModerationMessage {
    private Long reactionId;
    private Long tweetId;
    private String content;
    private String state; // APPROVE или DECLINE
    private String reason; // Причина модерации (если есть)
    
    // Для проверки на стоп-слова
    private boolean containsBadWords;
    private String[] foundBadWords;
}