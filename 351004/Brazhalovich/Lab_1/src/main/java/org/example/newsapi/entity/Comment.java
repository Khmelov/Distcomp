package org.example.newsapi.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseEntity {
    private Long newsId; // FK
    private String content;
}