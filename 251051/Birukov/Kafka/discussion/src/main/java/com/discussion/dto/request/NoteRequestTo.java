package com.discussion.dto.request;

import com.discussion.entity.Note;
import lombok.Data;

@Data
public class NoteRequestTo {
	
	private Long tweetId;
	private String content;
	private Long id;
	
	public NoteRequestTo() {}
	
	public NoteRequestTo(Long tweetId, String content) {
		this.tweetId = tweetId;
		this.content = content;
	}
	
	public Long getTweetId() { return tweetId; }
	public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
	
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
}