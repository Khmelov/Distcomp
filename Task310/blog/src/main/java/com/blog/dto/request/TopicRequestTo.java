package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class TopicRequestTo {
    @NotNull(message = "Editor ID is required")
    private Long editorId;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    private String content;

    private Set<Long> tagIds;

    // Конструкторы
    public TopicRequestTo() {}

    public TopicRequestTo(Long editorId, String title, String content, Set<Long> tagIds) {
        this.editorId = editorId;
        this.title = title;
        this.content = content;
        this.tagIds = tagIds;
    }

    // Геттеры и сеттеры
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

    public Set<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds;
    }
}