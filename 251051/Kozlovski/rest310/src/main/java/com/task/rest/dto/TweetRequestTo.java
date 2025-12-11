package com.task.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TweetRequestTo {
    @NotNull(message = "Writer ID cannot be null")
    private Long writerId;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 4, max = 64, message = "Title must be at most 64 characters")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 2048, message = "Content must be at most 2048 characters")
    private String content;

    public TweetRequestTo() {}

    public Long getWriterId() { return writerId; }
    public void setWriterId(Long writerId) { this.writerId = writerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
