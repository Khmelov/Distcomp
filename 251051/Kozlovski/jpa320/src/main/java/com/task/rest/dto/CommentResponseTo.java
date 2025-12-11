package com.task.rest.dto;

import java.time.LocalDateTime;

public class CommentResponseTo {
    private Long id;
    private Long tweetId;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;

    public CommentResponseTo() {}

    public CommentResponseTo(Long id, Long tweetId, String content, LocalDateTime created, LocalDateTime modified) {
        this.id = id;
        this.tweetId = tweetId;
        this.content = content;
        this.created = created;
        this.modified = modified;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
}
