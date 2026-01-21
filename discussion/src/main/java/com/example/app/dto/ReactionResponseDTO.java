package com.example.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponseDTO {
    
    private Long id;
    private Long tweetId;
    private String content;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}