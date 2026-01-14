package com.socialnetwork.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MessageRequestTo {

    @NotBlank(message = "Country cannot be blank")
    @Size(min = 2, max = 2, message = "Country code must be 2 characters")
    private String country = "US"; // Значение по умолчанию

    @NotNull(message = "Tweet ID is required")
    private Long tweetId;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;

    public MessageRequestTo() {}

    public MessageRequestTo(String country, Long tweetId, String content) {
        this.country = country;
        this.tweetId = tweetId;
        this.content = content;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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