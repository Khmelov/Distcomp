package com.example.kafkademo.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoteResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long tweetId;
}