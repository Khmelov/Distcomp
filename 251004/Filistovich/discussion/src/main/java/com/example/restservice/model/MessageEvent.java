package com.example.restservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent {
    private Long id;
    private Long articleId;
    private String content;
    private Status status;
    private LocalDateTime created;
    private String type;
}