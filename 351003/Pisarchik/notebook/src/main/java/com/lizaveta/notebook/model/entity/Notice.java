package com.lizaveta.notebook.model.entity;

public final class Notice {

    private final Long id;
    private final Long storyId;
    private final String content;

    public Notice(final Long id, final Long storyId, final String content) {
        this.id = id;
        this.storyId = storyId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Long getStoryId() {
        return storyId;
    }

    public String getContent() {
        return content;
    }

    public Notice withId(final Long newId) {
        return new Notice(newId, storyId, content);
    }

    public Notice withStoryId(final Long newStoryId) {
        return new Notice(id, newStoryId, content);
    }

    public Notice withContent(final String newContent) {
        return new Notice(id, storyId, newContent);
    }
}
