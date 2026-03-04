package com.example.demo.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class IssueRequestTo {
    @NotBlank
    @Size(min = 5, max = 50)
    @JsonProperty("title")
    private String title;

    @NotBlank
    @JsonProperty("authorId")
    private Long authorId;

    @NotBlank
    @Size(min = 2, max = 50)
    @JsonProperty("content")
    private String content;
}
