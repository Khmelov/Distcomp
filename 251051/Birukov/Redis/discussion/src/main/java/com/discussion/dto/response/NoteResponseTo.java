package com.discussion.dto.response;

import com.discussion.entity.Note;
import lombok.Data;

@Data
public class NoteResponseTo {
	
	private Long id;
	private Long tweetId;
	private String content;
	private Note.NoteState state;
	
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
	
	public Note.NoteState getState() { return state; }
	public void setState(Note.NoteState state) { this.state = state; }
}