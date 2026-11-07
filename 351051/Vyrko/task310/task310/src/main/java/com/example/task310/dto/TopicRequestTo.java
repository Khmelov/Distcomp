package com.example.task310.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Данные темы, которые приходят от клиента.
public class TopicRequestTo {

    @NotNull
    private Long editorId;

    @NotBlank
    @Size(min = 2, max = 64)
    private String title;

    @NotBlank
    @Size(min = 4, max = 2048)
    private String content;

    // Конструктор без параметров.
    public TopicRequestTo() {
    }

    // Конструктор для заполнения данных темы.
    public TopicRequestTo(Long editorId, String title, String content) {
        this.editorId = editorId;
        this.title = title;
        this.content = content;
    }

    // Методы доступа к полям DTO.
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
}