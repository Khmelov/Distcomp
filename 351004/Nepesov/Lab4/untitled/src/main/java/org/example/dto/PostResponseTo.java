package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.PostState;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseTo {
    private Long id;
    private Long newsId;
    private String content;
    private PostState state;
}