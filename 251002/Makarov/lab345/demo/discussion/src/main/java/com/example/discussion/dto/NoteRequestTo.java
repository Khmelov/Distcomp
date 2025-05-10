package com.example.discussion.dto;


import lombok.Getter;

import lombok.Setter;

@Setter
@Getter
public class NoteRequestTo {
    private Long id;
    private String content;
    private Long articleId;
    private String country; // Параметр для микросервиса discussion
}
