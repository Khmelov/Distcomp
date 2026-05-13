package com.example.publisher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class TweetResponseTo {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    private Set<Long> stickerIds;
}