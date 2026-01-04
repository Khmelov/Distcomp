package com.example.entitiesapp.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Post extends BaseEntity {
    private Long articleId;
    private String content;
}