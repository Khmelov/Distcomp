package com.restApp.dto;

import java.time.Instant;
import java.util.List;

public class NewsResponseTo {
    private Long id;
    private String title;
    private String content;
    private Instant createdAt;
    private Instant modifiedAt;
    private Long authorId;
    private List<CommentResponseTo> comments;
    private List<MarkResponseTo> marks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public List<CommentResponseTo> getComments() {
        return comments;
    }

    public void setComments(List<CommentResponseTo> comments) {
        this.comments = comments;
    }

    public List<MarkResponseTo> getMarks() {
        return marks;
    }

    public void setMarks(List<MarkResponseTo> marks) {
        this.marks = marks;
    }
}
