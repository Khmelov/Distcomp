package com.restApp.model;

import java.time.Instant;

public class Comment extends BaseEntity {
    private String content;
    private Instant timestamp;

    private Long newsId;
    private String authorLogin;

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

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
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
