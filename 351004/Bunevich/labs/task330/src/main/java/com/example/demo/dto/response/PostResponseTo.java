package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonRootName("post")
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class PostResponseTo {
    private long id;
    private long storyId;
    private String content;
}
