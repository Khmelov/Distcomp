package com.discussion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NoteResponseTo {
	
	private Long id;
	private Long tweetId;
	private String content;
	
	public NoteResponseTo() {}
	
	public NoteResponseTo(Long id, Long tweetId, String content) {
		this.id = id;
		this.tweetId = tweetId;
		this.content = content;
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public Long getTweetId() { return tweetId; }
	public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
	
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
}