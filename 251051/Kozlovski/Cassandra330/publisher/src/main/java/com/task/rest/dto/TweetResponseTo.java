package com.task.rest.dto;


import java.time.LocalDateTime;
import java.util.List;

public class TweetResponseTo {
    private Long id;
    private Long writerId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    private List<Long> markIds;

    public TweetResponseTo() {}

    public TweetResponseTo(Long id, Long writerId, String title, String content, LocalDateTime created, LocalDateTime modified, List<Long> markIds) {
        this.id = id;
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
        this.markIds = markIds;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWriterId() { return writerId; }
    public void setWriterId(Long writerId) { this.writerId = writerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
    public List<Long> getMarkIds() { return markIds; }
    public void setMarkIds(List<Long> markIds) { this.markIds = markIds; }
}
