package com.example.forum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.forum.entity.PostState;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long topicId;
    private Long id;
    private String content;
    private PostState state;
}
