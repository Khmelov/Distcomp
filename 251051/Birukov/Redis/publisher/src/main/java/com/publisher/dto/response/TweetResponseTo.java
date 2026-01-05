package com.publisher.dto.response;

import com.publisher.entity.Tweet;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TweetResponseTo {
	
	private Long id;
	private Long writerId;
	private String title;
	private String content;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime created;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime modified;
	
	public TweetResponseTo() {}
	
	public TweetResponseTo(Tweet tweet) {
		this.id = tweet.getId();
		this.writerId = tweet.getWriterId();
		this.title = tweet.getTitle();
		this.content = tweet.getContent();
		this.created = tweet.getCreated();
		this.modified = tweet.getModified();
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