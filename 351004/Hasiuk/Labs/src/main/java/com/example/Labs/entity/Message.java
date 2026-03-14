package com.example.Labs.entity;

import lombok.Data;

@Data
public class Message {
    private Long id;
    private Long storyId;
    private String content;
}