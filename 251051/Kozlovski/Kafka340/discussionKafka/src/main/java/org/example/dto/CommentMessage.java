package org.example.dto;

public class CommentMessage {
    private Long id;
    private Long tweetId;
    private String country;
    private String content;
    private String state; // PENDING, APPROVE, DECLINE

    public CommentMessage() {}

    public CommentMessage(Long id, Long tweetId, String country, String content, String state) {
        this.id = id;
        this.tweetId = tweetId;
        this.country = country;
        this.content = content;
        this.state = state;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}