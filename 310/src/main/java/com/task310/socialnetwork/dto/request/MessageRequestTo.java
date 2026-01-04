package com.task310.socialnetwork.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageRequestTo {
    @NotNull(message = "Tweet ID is required")
    @JsonProperty("tweetId")
    private Long tweetId;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    @JsonProperty("content")
    private String content;

    public MessageRequestTo() {}

    public MessageRequestTo(Long tweetId, String content) {
        this.tweetId = tweetId;
        this.content = content;
    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}