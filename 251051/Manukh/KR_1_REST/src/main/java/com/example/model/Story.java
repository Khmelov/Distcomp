package com.example.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Story extends BaseEntity {
    private Long editorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    private Set<Long> markIds = new HashSet<>();

    public Story() {}

    public Story(Long id, Long editorId, String title, String content) {
        this.id = id;
        this.editorId = editorId;
        this.title = title;
        this.content = content;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
        this.modified = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.modified = LocalDateTime.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.modified = LocalDateTime.now();
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public Set<Long> getMarkIds() {
        return markIds;
    }

    public void setMarkIds(Set<Long> markIds) {
        this.markIds = markIds;
    }

    public void addMark(Long markId) {
        this.markIds.add(markId);
    }

    public void removeMark(Long markId) {
        this.markIds.remove(markId);
    }
}