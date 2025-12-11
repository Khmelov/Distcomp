package org.example.dto;

import java.time.LocalDateTime;

public class CommentResponseTo {

    private Long id;
    private Long tweetId;
    private String country;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;

    public CommentResponseTo() {}

    public CommentResponseTo(Long id, Long tweetId, String country, String content, LocalDateTime created, LocalDateTime modified) {
        this.id = id;
        this.tweetId = tweetId;
        this.country = country;
        this.content = content;
        this.created = created;
        this.modified = modified;
    }

    public Long getId() { return id; }
    public Long getTweetId() { return tweetId; }
    public String getCountry() { return country; }
    public String getContent() { return content; }
    public LocalDateTime getCreated() { return created; }
    public LocalDateTime getModified() { return modified; }

    public void setId(Long id) { this.id = id; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public void setCountry(String country) { this.country = country; }
    public void setContent(String content) { this.content = content; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
}