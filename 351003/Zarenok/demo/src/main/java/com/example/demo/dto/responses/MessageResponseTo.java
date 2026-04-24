package com.example.demo.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class MessageResponseTo {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("issueId")
    private Long issueId;
    @JsonProperty("content")
    private String content;
}
