package com.example.discussion.dto;

import java.time.LocalDateTime;

public class NoticeResponseTo {
    private Long id;
    private Long newsId;
    private String content;
    private String state;
    private LocalDateTime created;
    private LocalDateTime modified;

    public NoticeResponseTo() {}
    public NoticeResponseTo(Long id, Long newsId, String content, String state, LocalDateTime created, LocalDateTime modified) {
        this.id = id;
        this.newsId = newsId;
        this.content = content;
        this.state = state;
        this.created = created;
        this.modified = modified;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
}