package com.restApp.dto;

import java.util.List;

public class NewsRequestTo {
    private String title;
    private String content;
    private Long authorId;
    private List<Long> markIds;

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

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public List<Long> getMarkIds() {
        return markIds;
    }

    public void setMarkIds(List<Long> markIds) {
        this.markIds = markIds;
    }
}
