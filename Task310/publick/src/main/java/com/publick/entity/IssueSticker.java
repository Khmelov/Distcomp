package com.publick.entity;

public class IssueSticker {
    private Long issueId;
    private Long stickerId;

    public IssueSticker() {
    }

    public IssueSticker(Long issueId, Long stickerId) {
        this.issueId = issueId;
        this.stickerId = stickerId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getStickerId() {
        return stickerId;
    }

    public void setStickerId(Long stickerId) {
        this.stickerId = stickerId;
    }
}