package com.example.restApi.model;

public class Comment extends BaseEntity {
    private Long issueId;
    private String content;

    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
