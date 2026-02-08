package com.example.demo.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.ZonedDateTime;
@Value
public class IssueResponseTo {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("authorId")
    private Long authorId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("content")
    private String content;
    @JsonProperty("created")
    private ZonedDateTime created;
    @JsonProperty("modified")
    private ZonedDateTime modified;
}
