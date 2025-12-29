package com.jpa.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NoteRequestTo {
	
	@NotNull(message = "Tweet ID is required")
	private Long tweetId;
	
	@NotBlank(message = "Content is required")
	@Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
	private String content;
	
	public NoteRequestTo() {}
	
	public NoteRequestTo(Long tweetId, String content) {
		this.tweetId = tweetId;
		this.content = content;
	}
	
	public Long getTweetId() { return tweetId; }
	public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
	
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
}