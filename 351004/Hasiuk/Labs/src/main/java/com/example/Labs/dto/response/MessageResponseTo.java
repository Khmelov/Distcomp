package com.example.Labs.dto.response;

import lombok.Data;

@Data
public class MessageResponseTo {
    private Long id;
    private Long storyId;
    private String content;
}