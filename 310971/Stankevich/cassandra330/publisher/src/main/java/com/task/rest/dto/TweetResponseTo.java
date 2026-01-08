package com.task.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetResponseTo {
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private List<String> marks;
}
