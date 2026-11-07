package com.distcomp.model;

/**
 * A remark attached to an issue (the "many" side of the Issue-Notice relation).
 */
public class Notice extends AbstractEntity {

    private Long issueId;

    private String content;

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

    @Override
    public String toString() {
        return "Notice{id=" + getId() + ", issueId=" + issueId + "}";
    }
}
