package com.example.discussion.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NoteResponseTo {
    private Long id;
    private String content;
    private Long articleId;
}
