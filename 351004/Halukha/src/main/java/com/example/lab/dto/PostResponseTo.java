package com.example.lab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostResponseTo {

    private Long id;
    private Long newsId;
    private String content;
    
    public PostResponseTo(
            @JsonProperty("id") Long id,
            @JsonProperty("newsId") Long newsId,
            @JsonProperty("content") String content) {
        this.id = id;
        this.newsId = newsId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Long getNewsId() {
        return newsId;
    }

    public String getContent() {
        return content;
    }
}
