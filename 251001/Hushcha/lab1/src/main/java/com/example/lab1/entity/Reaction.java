package com.example.lab1.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Reaction implements BaseEntity {

    private Long id;

    private Long newsId;

    private String content;
}
