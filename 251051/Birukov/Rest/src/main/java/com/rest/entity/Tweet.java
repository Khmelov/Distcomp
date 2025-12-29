package com.rest.entity;

import java.time.LocalDateTime;

public class Tweet {
    
    private Long id;
    private Long writerId;
    private String title;
    private String content;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified = LocalDateTime.now();
    
    public Tweet() {}
    
    public Tweet(Long writerId, String title, String content) {
		this();
        this.writerId = writerId;
        this.title = title;
        this.content = content;
		this.created = LocalDateTime.now();
		this.modified = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getWriterId() { return writerId; }
    public void setWriterId(Long writerId) { this.writerId = writerId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
	
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    
    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
}