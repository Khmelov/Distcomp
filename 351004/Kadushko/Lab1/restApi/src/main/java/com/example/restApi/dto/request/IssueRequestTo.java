package com.example.restApi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class IssueRequestTo {
    private Long id;

    @NotNull(message = "Editor ID cannot be null")
    private Long editorId;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;

    @Size(max = 2048, message = "Content cannot exceed 2048 characters")
    private String content;

    private Set<Long> markerIds;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEditorId() { return editorId; }
    public void setEditorId(Long editorId) { this.editorId = editorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Set<Long> getMarkerIds() { return markerIds; }
    public void setMarkerIds(Set<Long> markerIds) { this.markerIds = markerIds; }
}
