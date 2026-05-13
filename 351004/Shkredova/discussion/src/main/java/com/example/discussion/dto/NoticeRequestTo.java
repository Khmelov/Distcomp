package com.example.discussion.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NoticeRequestTo {
    private Long id;

    @NotNull(message = "News ID is required")
    private Long newsId;

    @NotBlank(message = "Content is required")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;

    public NoticeRequestTo() {}

    public NoticeRequestTo(Long id, Long newsId, String content) {
        this.id = id;
        this.newsId = newsId;
        this.content = content;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}