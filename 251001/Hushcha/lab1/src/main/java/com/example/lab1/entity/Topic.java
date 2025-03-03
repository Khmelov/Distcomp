package com.example.lab1.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class Topic implements BaseEntity{

    private Long id;

    private String title;

    private String content;

    private Long userId;

    private LocalDateTime created;

    private LocalDateTime modified;

}
