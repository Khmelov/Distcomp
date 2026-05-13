package com.example.task310.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NewsResponseTo {
    private Long id;
    private Long creatorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
}