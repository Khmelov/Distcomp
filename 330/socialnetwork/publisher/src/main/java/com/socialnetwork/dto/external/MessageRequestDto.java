package com.socialnetwork.dto.external;

public class MessageRequestDto {
    private String country;
    private Long tweetId;
    private String content;

    public MessageRequestDto() {}

    public MessageRequestDto(String country, Long tweetId, String content) {
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