package com.socialnetwork.discussion.dto.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KafkaMessageResponse {
    private UUID requestId;
    private boolean success;
    private String error;
    private Long messageId;
    private String country;
    private Long tweetId;
    private String content;
    private String state; // PENDING, APPROVE, DECLINE
    private List<KafkaMessageResponse> messages;

    public KafkaMessageResponse() {}

    // Геттеры и сеттеры
    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

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

    public List<KafkaMessageResponse> getMessages() {
        return messages;
    }

    public void setMessages(List<KafkaMessageResponse> messages) {
        this.messages = messages;
    }
}