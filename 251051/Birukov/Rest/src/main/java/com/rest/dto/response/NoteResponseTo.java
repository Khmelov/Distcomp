package com.rest.dto.response;

import com.rest.entity.Note;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoteResponseTo {
    
    private Long id;
    private Long tweetId;
    private String content;
	
	public NoteResponseTo() {}
	
    public NoteResponseTo(Note note) {
        this.id = note.getId();
        this.tweetId = note.getTweetId();
        this.content = note.getContent();
    }
    
    public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
    public Long getTweetId() { return tweetId; }
	public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
	
    public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
}