package com.socialnetwork.discussion.model;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.Size;

@Table(value = "tbl_message")
public class Message {

    @PrimaryKeyColumn(
            name = "country",
            type = PrimaryKeyType.PARTITIONED,
            ordinal = 0
    )
    private String country;

    @PrimaryKeyColumn(
            name = "tweet_id",
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING,
            ordinal = 1
    )
    private Long tweetId;

    @PrimaryKeyColumn(
            name = "id",
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING,
            ordinal = 2
    )
    private Long id;

    @Column("content")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;

    @Column("state")
    private String state = "PENDING"; // PENDING, APPROVE, DECLINE

    public Message() {}

    public Message(String country, Long tweetId, Long id, String content) {
        this.country = country;
        this.tweetId = tweetId;
        this.id = id;
        this.content = content;
        this.state = "PENDING";
    }

    public Message(String country, Long tweetId, Long id, String content, String state) {
        this.country = country;
        this.tweetId = tweetId;
        this.id = id;
        this.content = content;
        this.state = state;
    }

    // Getters and Setters
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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