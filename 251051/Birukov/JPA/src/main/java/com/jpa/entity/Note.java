package com.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_note", schema = "distcomp")
public class Note {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "note_seq")
	@SequenceGenerator(name = "note_seq", schema = "distcomp", 
					   sequenceName = "seq_note_id", allocationSize = 1)
	private Long id;
	
	@Column(name = "tweet_id", nullable = false, insertable = false, updatable = false)
	private Long tweetId;
	
	@Column(name = "content", nullable = false, length = 2048)
	@Size(min = 2, max = 2048)
	private String content;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tweet_id", nullable = false,
				foreignKey = @ForeignKey(name = "fk_note_tweet"))
	private Tweet tweet;
	
	public Note() {}
	
	public Note(Long tweetId, String content) {
		this.tweetId = tweetId;
		this.content = content;
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public Long getTweetId() { return tweetId; }
	public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
	
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
	
	public Tweet getTweet() { return tweet; }
	public void setTweet(Tweet tweet) { this.tweet = tweet; }
}