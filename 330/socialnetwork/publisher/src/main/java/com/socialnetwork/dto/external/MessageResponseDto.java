package com.socialnetwork.dto.external;

public class MessageResponseDto {
    private String country;
    private Long tweetId;
    private Long id;
    private String content;

    public MessageResponseDto() {}

    public MessageResponseDto(String country, Long tweetId, Long id, String content) {
        this.country = country;
        this.tweetId = tweetId;
        this.id = id;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}