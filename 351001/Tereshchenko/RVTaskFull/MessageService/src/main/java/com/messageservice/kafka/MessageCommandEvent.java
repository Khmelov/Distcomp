package com.messageservice.kafka;

import com.messageservice.models.MessageState;

public class MessageCommandEvent {

    private String correlationId;
    private MessageOperation operation;
    private Long messageId;
    private Long tweetId;
    private String content;
    private MessageState state;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public MessageOperation getOperation() {
        return operation;
    }

    public void setOperation(MessageOperation operation) {
        this.operation = operation;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
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

    public MessageState getState() {
        return state;
    }

    public void setState(MessageState state) {
        this.state = state;
    }
}
