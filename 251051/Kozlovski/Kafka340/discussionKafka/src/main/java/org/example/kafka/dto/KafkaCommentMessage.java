package org.example.kafka.dto;

import java.time.LocalDateTime;

public class KafkaCommentMessage {

    private Long id;
    private Long tweetId;
    private String country;
    private String content;
    private String state;
    private LocalDateTime created;
    private String operation;

    public KafkaCommentMessage() {}

    public KafkaCommentMessage(Long id, Long tweetId, String country, String content, String operation) {
        this.id = id;
        this.tweetId = tweetId;
        this.country = country;
        this.content = content;
        this.operation = operation;
        this.state = "PENDING";
        this.created = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
}