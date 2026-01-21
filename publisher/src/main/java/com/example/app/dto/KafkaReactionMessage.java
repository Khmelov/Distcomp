package com.example.app.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaReactionMessage {
    private Long id;
    private Long tweetId;
    private String content;
    private String country;
    private String state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String operation; // CREATE, UPDATE, DELETE, MODERATE
}