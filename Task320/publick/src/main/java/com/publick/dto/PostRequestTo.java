package com.publick.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostRequestTo {
    @NotNull
    private Long issueId;

    @NotBlank
    @Size(min = 2, max = 2048)
    private String content;

    public PostRequestTo() {
    }

    public PostRequestTo(Long issueId, String content) {
        this.issueId = issueId;
        this.content = content;
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