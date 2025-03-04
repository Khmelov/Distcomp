package com.bsuir.dc.dto.request;

import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class TopicRequestTo {
    private long id;
    private long authorId;
    private List<String> labels = new ArrayList<>();

    @Size(min = 2, max = 64, message = "Title size must be between 2..64 characters")
    private String title;

    @Size(min = 4, max = 2048, message = "Content size must be between 2..64 characters")
    private String content;

    public void setId(long id) { this.id = id; }
    public long getId() { return id; }

    public void setAuthorId(long authorId) { this.authorId = authorId; }
    public long getAuthorId() { return authorId; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }

    public void setLabels(List<String> labels) { this.labels = labels; }
    public List<String> getLabels() { return labels; }
}
