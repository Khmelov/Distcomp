package com.example.kafkademo.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TweetResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long creatorId;
    private List<Long> noteIds;
    private List<Long> markerIds;
}