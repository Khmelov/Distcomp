package com.example.lab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequestTo {

    private Long newsId;

    @NotBlank
    @Size(min = 2, max = 2048)
    private String content;

    public PostRequestTo(
            @JsonProperty("newsId") Long newsId,
            @JsonProperty("content") String content) {
        this.newsId = newsId;
        this.content = content;
    }

    public Long getNewsId() {
        return newsId;
    }

    public String getContent() {
        return content;
    }
}
