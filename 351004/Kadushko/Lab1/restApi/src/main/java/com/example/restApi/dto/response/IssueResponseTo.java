package com.example.restApi.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public class IssueResponseTo {
    private Long id;
    private Long editorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    private Set<Long> markerIds;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEditorId() { return editorId; }
    public void setEditorId(Long editorId) { this.editorId = editorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }

    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }

    public Set<Long> getMarkerIds() { return markerIds; }
    public void setMarkerIds(Set<Long> markerIds) { this.markerIds = markerIds; }
}
