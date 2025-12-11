package com.task.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentRequestTo {
    @NotNull(message = "Tweet ID cannot be null")
    private Long tweetId;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 2048, message = "Content must be at most 2048 characters")
    private String content;

    public CommentRequestTo() {}

    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}