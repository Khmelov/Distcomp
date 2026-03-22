package com.example.restApi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentRequestTo {
    private Long id;

    @NotNull(message = "Issue ID cannot be null")
    private Long issueId;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
