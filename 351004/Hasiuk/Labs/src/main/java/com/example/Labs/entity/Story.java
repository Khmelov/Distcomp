package com.example.Labs.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Story {
    private Long id;
    private Long editorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
}