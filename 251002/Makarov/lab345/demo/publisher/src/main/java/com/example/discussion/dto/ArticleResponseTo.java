package com.example.discussion.dto;

import com.example.discussion.model.Mark;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponseTo {
    private Long id;
    private String title;
    private String content;
    private Long creatorId;
    private List<Mark> marks;
}
