package com.task310.socialnetwork.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

public class TweetRequestTo {
    @NotNull(message = "User ID is required")
    @JsonProperty("userId")
    private Long userId;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    @JsonProperty("title")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    @JsonProperty("content")
    private String content;

    @JsonProperty("labelIds")
    private Set<Long> labelIds;

    public TweetRequestTo() {}

    public TweetRequestTo(Long userId, String title, String content, Set<Long> labelIds) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.labelIds = labelIds;
    }

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

    public Set<Long> getLabelIds() {
        return labelIds;
    }

    public void setLabelIds(Set<Long> labelIds) {
        this.labelIds = labelIds;
    }
}