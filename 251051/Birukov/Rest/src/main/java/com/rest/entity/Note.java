package com.rest.entity;

public class Note {
    
    private Long id;
    private Long tweetId;
    private String content;
    
    public Note() {}
    
    public Note(Long tweetId, String content) {
        this.tweetId = tweetId;
        this.content = content;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}