package org.example.newsapi.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentKafkaMessage {
    private Long id;
    private Long newsId;
    private String content;
    private String state; // PENDING, APPROVE, DECLINE
}