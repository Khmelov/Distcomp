package com.example.task320.dto;

import java.time.LocalDateTime;

// Данные темы, которые возвращаются клиенту.
public class TopicResponseTo {

    private Long id;
    private Long editorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;

    // Конструктор без параметров.
    public TopicResponseTo() {
    }

    // Конструктор для заполнения всех полей ответа.
    public TopicResponseTo(
            Long id,
            Long editorId,
            String title,
            String content,
            LocalDateTime created,
            LocalDateTime modified) {
        this.id = id;
        this.editorId = editorId;
        this.title = title;
        this.content = content;
        this.created = created;
        this.modified = modified;
    }

    // Методы доступа к полям DTO.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
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
}