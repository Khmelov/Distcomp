package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponseTo {
    private Long id;
    private Long articleId;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
}