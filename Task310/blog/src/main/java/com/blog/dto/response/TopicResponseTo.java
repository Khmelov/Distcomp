package com.blog.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public class TopicResponseTo {
    private Long id;
    private Long writerId; // Изменено с editorId на writerId
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    private Set<Long> tagIds;

    // Конструкторы
    public TopicResponseTo() {}

    public TopicResponseTo(Long id, Long writerId, String title, String content,
                           LocalDateTime created, LocalDateTime modified, Set<Long> tagIds) {
        this.id = id;
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
        this.tagIds = tagIds;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWriterId() {
        return writerId;
    }

    public void setWriterId(Long writerId) {
        this.writerId = writerId;
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

    public Set<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds;
    }

    // Для совместимости с существующим кодом
    public Long getEditorId() {
        return writerId;
    }

    public void setEditorId(Long editorId) {
        this.writerId = editorId;
    }
}