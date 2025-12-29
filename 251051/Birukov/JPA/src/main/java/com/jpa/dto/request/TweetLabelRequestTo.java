package com.jpa.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TweetLabelRequestTo {
	
	@NotNull(message = "Tweet ID is required")
	private Long tweetId;
	
	@NotNull(message = "Label ID is required")
	private Long labelId;
	
	public TweetLabelRequestTo() {}
	
	public TweetLabelRequestTo(Long tweetId, Long labelId) {
		this.tweetId = tweetId;
		this.labelId = labelId;
	}
	
	public Long getTweetId() { return tweetId; }
	public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
	
	public Long getLabelId() { return labelId; }
	public void setLabelId(Long labelId) { this.labelId = labelId; }
}