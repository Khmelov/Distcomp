package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponseTo {
    private Long id;
    private Long writerId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
}