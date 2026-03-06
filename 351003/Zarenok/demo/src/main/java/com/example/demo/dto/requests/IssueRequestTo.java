package com.example.demo.dto.requests;

import com.example.demo.config.StringDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class IssueRequestTo {
    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    @JsonProperty("title")
    private String title;

    @NotNull(message = "Author ID is required")
    @JsonProperty("authorId")
    private Long authorId;

    @JsonProperty("content")
    @JsonDeserialize(using = StringDeserializer.class)
    private String content;

    private List<@NotBlank @Size(min = 2, max = 32) String> marks;
}
