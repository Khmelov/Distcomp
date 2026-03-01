package com.example.restApi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class ArticleRequestTo {
    @JsonProperty("userId")
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @JsonProperty("title")
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;

    @JsonProperty("content")
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    private String content;

    @JsonProperty("markerIds")
    private Set<Long> markerIds;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<Long> getMarkerIds() {
        return markerIds;
    }

    public void setMarkerIds(Set<Long> markerIds) {
        this.markerIds = markerIds;
    }
}
