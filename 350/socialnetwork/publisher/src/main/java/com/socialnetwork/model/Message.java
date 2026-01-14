package com.socialnetwork.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tbl_message", schema = "distcomp")
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;

    @Column(name = "content", nullable = false, length = 2048)
    @Size(min = 4, max = 2048)
    private String content;

    @Column(name = "state", nullable = false, length = 20)
    private String state = "PENDING"; // PENDING, APPROVE, DECLINE

    public Message() {
        super();
    }

    public Message(Tweet tweet, String content) {
        this.tweet = tweet;
        this.content = content;
        this.state = "PENDING";
    }

    public Message(Tweet tweet, String content, String state) {
        this.tweet = tweet;
        this.content = content;
        this.state = state;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}