package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponseTo {
    private Long id;
    private String title;
    private String content;
    private Long creatorId;
}
