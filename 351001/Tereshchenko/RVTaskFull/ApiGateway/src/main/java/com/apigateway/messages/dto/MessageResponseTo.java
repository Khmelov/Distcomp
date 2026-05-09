package com.apigateway.messages.dto;

import com.apigateway.messages.kafka.MessageResultEvent;
import com.apigateway.messages.kafka.MessageState;

public class MessageResponseTo {

    private Long id;
    private Long tweetId;
    private String content;
    private MessageState state;

    public static MessageResponseTo from(MessageResultEvent event) {
        MessageResponseTo response = new MessageResponseTo();
        response.setId(event.getMessageId());
        response.setTweetId(event.getTweetId());
        response.setContent(event.getContent());
        response.setState(event.getState());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
