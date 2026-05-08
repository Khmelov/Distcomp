package com.example.publisher.dto.response;

import lombok.Data;

@Data
public class NoteResponseTo {
    private Long id;
    private Long articleId;
    private String content;
}