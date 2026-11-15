
package com.example.task320.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

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

    // Список названий маркеров, связанных с темой.
    private List<String> markers;

    // Конструктор без параметров.
    public TopicRequestTo() {
    }

    // Конструктор для заполнения основных данных темы.
    public TopicRequestTo(Long editorId, String title, String content) {
        this.editorId = editorId;
        this.title = title;
        this.content = content;
    }

    // Возвращает идентификатор редактора.
    public Long getEditorId() {
        return editorId;
    }

    // Устанавливает идентификатор редактора.
    public void setEditorId(Long editorId) {
        this.editorId = editorId;
    }

    // Возвращает заголовок темы.
    public String getTitle() {
        return title;
    }

    // Устанавливает заголовок темы.
    public void setTitle(String title) {
        this.title = title;
    }

    // Возвращает содержимое темы.
    public String getContent() {
        return content;
    }

    // Устанавливает содержимое темы.
    public void setContent(String content) {
        this.content = content;
    }

    // Возвращает список маркеров.
    public List<String> getMarkers() {
        return markers;
    }

    // Устанавливает список маркеров.
    public void setMarkers(List<String> markers) {
        this.markers = markers;
    }
}
