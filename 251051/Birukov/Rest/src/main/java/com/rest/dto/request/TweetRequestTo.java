package com.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TweetRequestTo {
    
    @NotNull(message = "Writer ID is required")
    private Long writerId;
    
    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;
    
    @NotBlank(message = "Content is required")
    @Size(min = 4, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;
    
	public TweetRequestTo() {}
    
    public TweetRequestTo(Long writerId, String title, String content) {
		this();
        this.writerId = writerId;
        this.title = title;
        this.content = content;
    }
	
    public Long getWriterId() { return writerId; }
    public void setWriterId(Long writerId) { this.writerId = writerId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}