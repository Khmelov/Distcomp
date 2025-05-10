package com.example.discussion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class NoteResponseTo {
    private Long id;
    private String content;
    private Long articleId;
}
