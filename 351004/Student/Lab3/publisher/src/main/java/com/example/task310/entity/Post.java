package com.example.task310.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long id;
    private Long issueId;
    private String content;
}