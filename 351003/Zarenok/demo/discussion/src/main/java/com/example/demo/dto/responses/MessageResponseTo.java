package com.example.demo.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageResponseTo {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("issueId")
    private Long issueId;
    @JsonProperty("content")
    private String content;
    @JsonProperty("state")
    private String state;
}
