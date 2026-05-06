package com.example.demo.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
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
