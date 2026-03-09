package org.example.newsapi.dto.response;

import lombok.Data;

@Data
public class CommentResponseTo {
    private Long id;
    private Long newsId;
    private String content;
}