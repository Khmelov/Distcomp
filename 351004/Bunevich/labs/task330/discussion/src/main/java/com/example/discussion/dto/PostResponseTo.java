package com.example.discussion.dto;

import lombok.Data;

@Data
public class PostResponseTo {
    private Long id;
    private Long storyId;
    private String content;
}
