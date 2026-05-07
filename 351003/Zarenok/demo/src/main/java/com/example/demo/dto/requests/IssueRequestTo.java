package com.example.demo.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class IssueRequestTo {
    @JsonProperty("title")
    private String title;
    @JsonProperty("authorId")
    private Long authorId;
    @JsonProperty("content")
    private String content;
}
