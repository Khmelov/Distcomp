package com.example.restApi.model;

import java.util.HashSet;
import java.util.Set;

public class Article extends BaseEntity {
    private Long userId;
    private String title;
    private String content;
    private Set<Long> markerIds = new HashSet<>();

    public Article() {
        super();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Set<Long> getMarkerIds() {
        return markerIds;
    }

    public void setMarkerIds(Set<Long> markerIds) {
        this.markerIds = markerIds;
    }
}
