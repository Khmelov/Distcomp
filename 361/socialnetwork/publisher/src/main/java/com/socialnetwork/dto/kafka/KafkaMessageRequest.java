package com.socialnetwork.dto.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KafkaMessageRequest {

    @JsonProperty("requestId")
    private UUID requestId;

    @JsonProperty("operation")
    private String operation; // CREATE, UPDATE, DELETE, GET, GET_ALL, GET_BY_TWEET

    @JsonProperty("messageId")
    private Long messageId;

    @JsonProperty("country")
    private String country;

    @JsonProperty("tweetId")
    private Long tweetId;

    @JsonProperty("content")
    private String content;

    public KafkaMessageRequest() {}

    // Геттеры и сеттеры с @JsonProperty
    @JsonProperty("requestId")
    public UUID getRequestId() {
        return requestId;
    }

    @JsonProperty("requestId")
    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    @JsonProperty("operation")
    public String getOperation() {
        return operation;
    }

    @JsonProperty("operation")
    public void setOperation(String operation) {
        this.operation = operation;
    }

    @JsonProperty("messageId")
    public Long getMessageId() {
        return messageId;
    }

    @JsonProperty("messageId")
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("tweetId")
    public Long getTweetId() {
        return tweetId;
    }

    @JsonProperty("tweetId")
    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }
}