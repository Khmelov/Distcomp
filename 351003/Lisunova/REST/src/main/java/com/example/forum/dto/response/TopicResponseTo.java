package com.example.forum.dto.response;

import java.time.OffsetDateTime;
import java.util.Set;

public class TopicResponseTo {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private Set<Long> markIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public OffsetDateTime getModified() {
        return modified;
    }

    public void setModified(OffsetDateTime modified) {
        this.modified = modified;
    }

    public Set<Long> getMarkIds() {
        return markIds;
    }

    public void setMarkIds(Set<Long> markIds) {
        this.markIds = markIds;
    }
}
