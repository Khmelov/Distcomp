package com.distcomp.publisher.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_tweet_marker")
public class TweetMarker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tweet_id")
    private Long tweetId;

    @Column(name = "marker_id")
    private Long markerId;

    public TweetMarker() {}

    public TweetMarker(Long id, Long tweetId, Long markerId) {
        this.id = id;
        this.tweetId = tweetId;
        this.markerId = markerId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }

    public Long getMarkerId() { return markerId; }
    public void setMarkerId(Long markerId) { this.markerId = markerId; }
}