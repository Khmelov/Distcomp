package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonRootName("story")
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class StoryResponseTo {
    private long id;
    private long writerId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;

    private List<TagResponseTo> tags;
}
