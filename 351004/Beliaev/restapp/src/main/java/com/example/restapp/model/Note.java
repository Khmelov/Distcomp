package com.example.restapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Note extends BaseEntity {
    private Long articleId;
    private String content;
}