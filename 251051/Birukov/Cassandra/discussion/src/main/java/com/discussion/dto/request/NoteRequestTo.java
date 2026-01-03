package com.discussion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NoteRequestTo {
	
	private Long tweetId;
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