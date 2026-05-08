package com.example.task310.entity;

import com.example.task310.enums.PostState;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Long id;
    private Long issueId;
    private String content;
    private PostState state;
}