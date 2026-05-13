package com.example.discussion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReactionRequestTo {

    @NotNull
    private Long tweetId;

    @Size(min = 2, max = 512)
    private String content;

    // Конструкторы
    public ReactionRequestTo() {}

    public ReactionRequestTo(Long tweetId, String content) {
        this.tweetId = tweetId;
        this.content = content;
    }

    // Геттеры и сеттеры
    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}