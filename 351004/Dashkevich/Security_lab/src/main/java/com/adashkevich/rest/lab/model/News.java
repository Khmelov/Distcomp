package com.adashkevich.rest.lab.model;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public class News extends BaseEntity {
    private Long editorId;
    private String title;
    private String content;
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private Set<Long> markerIds = new HashSet<>();

    public Long getEditorId() { return editorId; }
    public void setEditorId(Long editorId) { this.editorId = editorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public OffsetDateTime getCreated() { return created; }
    public void setCreated(OffsetDateTime created) { this.created = created; }

    public OffsetDateTime getModified() { return modified; }
    public void setModified(OffsetDateTime modified) { this.modified = modified; }

    public Set<Long> getMarkerIds() { return markerIds; }
    public void setMarkerIds(Set<Long> markerIds) {
        this.markerIds = markerIds == null ? new HashSet<>() : markerIds;
    }
}
