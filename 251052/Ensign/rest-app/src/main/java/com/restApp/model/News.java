package com.restApp.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class News extends BaseEntity {
    private String title;
    private String content;
    private Instant timestamp;

    private Long authorId;

    private List<Comment> comments = new ArrayList<>();
    private List<Mark> marks = new ArrayList<>();

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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public void setMarks(List<Mark> marks) {
        this.marks = marks;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
