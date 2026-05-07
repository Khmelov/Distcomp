package com.example.task310.dto;

import com.example.task310.model.NoticeState;
import java.time.LocalDateTime;

public class NoticeResponseTo {

    private Long id;
    private Long newsId;
    private String content;
    private NoticeState state;
    private LocalDateTime created;
    private LocalDateTime modified;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public NoticeState getState() { return state; }
    public void setState(NoticeState state) { this.state = state; }

    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }

    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
}