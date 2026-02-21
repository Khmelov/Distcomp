package com.example.demo.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Value;

@Data
public class MessageRequestTo {
    @JsonProperty("content")
    private String content;
    @JsonProperty("issueId")
    private Long issueId;
}
