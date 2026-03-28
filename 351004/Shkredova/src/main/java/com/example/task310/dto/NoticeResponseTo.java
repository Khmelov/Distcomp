package com.example.task310.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoticeResponseTo {
    private Long id;
    private Long newsId;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
}