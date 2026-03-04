package com.example.demo.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageRequestTo {

    @NotBlank
    @Size(min = 2, max = 50)
    @JsonProperty("content")
    private String content;

    @NotBlank
    @JsonProperty("issueId")
    private Long issueId;
}
