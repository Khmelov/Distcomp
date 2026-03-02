package com.lizaveta.notebook.model.entity;

import java.time.LocalDateTime;
import java.util.Set;

public final class Story {

    private final Long id;
    private final Long writerId;
    private final String title;
    private final String content;
    private final LocalDateTime created;
    private final LocalDateTime modified;
    private final Set<Long> markerIds;

    public Story(
            final Long id,
            final Long writerId,
            final String title,
            final String content,
            final LocalDateTime created,
            final LocalDateTime modified,
            final Set<Long> markerIds) {
        this.id = id;
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
        this.markerIds = markerIds == null ? Set.of() : Set.copyOf(markerIds);
    }

    public Long getId() {
        return id;
    }

    public Long getWriterId() {
        return writerId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public Set<Long> getMarkerIds() {
        return markerIds;
    }

    public Story withId(final Long newId) {
        return new Story(newId, writerId, title, content, created, modified, markerIds);
    }

    public Story withWriterId(final Long newWriterId) {
        return new Story(id, newWriterId, title, content, created, modified, markerIds);
    }

    public Story withTitle(final String newTitle) {
        return new Story(id, writerId, newTitle, content, created, modified, markerIds);
    }

    public Story withContent(final String newContent) {
        return new Story(id, writerId, title, newContent, created, modified, markerIds);
    }

    public Story withCreated(final LocalDateTime newCreated) {
        return new Story(id, writerId, title, content, newCreated, modified, markerIds);
    }

    public Story withModified(final LocalDateTime newModified) {
        return new Story(id, writerId, title, content, created, newModified, markerIds);
    }

    public Story withMarkerIds(final Set<Long> newMarkerIds) {
        return new Story(id, writerId, title, content, created, modified, newMarkerIds);
    }
}
