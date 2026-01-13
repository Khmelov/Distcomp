package com.publick.entity;

public class Post {
    private Long id;
    private Long issueId;
    private String content;

    public Post() {
    }

    public Post(Long id, Long issueId, String content) {
        this.id = id;
        this.issueId = issueId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}