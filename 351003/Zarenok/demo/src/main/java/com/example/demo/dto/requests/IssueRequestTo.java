package com.example.demo.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class IssueRequestTo {
    @NotBlank
    @Size(min = 2, max = 64)
    @JsonProperty("title")
    private String title;

    @NotNull
    @JsonProperty("authorId")
    private Long authorId;

    @JsonProperty("content")
    private String content;
}
