package com.publisher.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_tweet_label", schema = "distcomp")
public class TweetLabel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tweet_label_seq")
	@SequenceGenerator(name = "tweet_label_seq", schema = "distcomp", 
					   sequenceName = "seq_tweet_label_id", allocationSize = 1)
	private Long id;
	
	@Column(name = "tweet_id", nullable = false, insertable = false, updatable = false)
	private Long tweetId;
	
	@Column(name = "label_id", nullable = false, insertable = false, updatable = false)
	private Long labelId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tweet_id", nullable = false,
				foreignKey = @ForeignKey(name = "fk_tweet_label_tweet"))
	private Tweet tweet;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "label_id", nullable = false,
				foreignKey = @ForeignKey(name = "fk_tweet_label_label"))
	private Label label;
	
	public TweetLabel() {}
	
	public TweetLabel(Long tweetId, Long labelId) {
		this.tweetId = tweetId;
		this.labelId = labelId;
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public Long getTweetId() { return tweetId; }
	public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
	
	public Tweet getTweet() { return tweet; }
	public void setTweet(Tweet tweet) { this.tweet = tweet; }
	
	public Long getLabelId() { return labelId; }
	public void setLabelId(Long labelId) { this.labelId = labelId; }
	
	public Label getLabel() { return label; }
	public void setLabel(Label label) { this.label = label; }
}