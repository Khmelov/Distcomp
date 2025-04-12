package com.example.rest.dto.updateDto;

import lombok.Data;

import java.util.List;

@Data
public class StoryUpdateTo {
    private long id;
    private long userId;
    private String title;
    private String content;
    private List<String> labels;
}
