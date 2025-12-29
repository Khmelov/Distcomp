package com.jpa.dto.response;

import com.jpa.entity.TweetLabel;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TweetLabelResponseTo {
	
	private Long id;
	private Long tweetId;
	private Long labelId;
	
	public TweetLabelResponseTo() {}
	
	public TweetLabelResponseTo(TweetLabel tweetLabel) {
		this.id = tweetLabel.getId();
		this.tweetId = tweetLabel.getTweetId();
		this.labelId = tweetLabel.getLabelId();
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public Long getTweetId() { return tweetId; }
	public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
	
	public Long getLabelId() { return labelId; }
	public void setLabelId(Long labelId) { this.labelId = labelId; }
}