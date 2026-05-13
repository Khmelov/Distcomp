package com.messageservice.kafka;

import com.messageservice.dtos.MessageResponseTo;
import com.messageservice.models.MessageState;

public class MessageResultEvent {

    private String correlationId;
    private MessageOperation operation;
    private boolean success;
    private Long messageId;
    private Long tweetId;
    private String content;
    private MessageState state;
    private String errorCode;
    private String errorMessage;

    public static MessageResultEvent success(MessageCommandEvent command, MessageResponseTo response) {
        MessageResultEvent event = base(command);
        event.setSuccess(true);
        event.setMessageId(response.getId());
        event.setTweetId(response.getTweetId());
        event.setContent(response.getContent());
        event.setState(response.getState());
        return event;
    }

    public static MessageResultEvent success(MessageCommandEvent command) {
        MessageResultEvent event = base(command);
        event.setSuccess(true);
        event.setMessageId(command.getMessageId());
        event.setTweetId(command.getTweetId());
        event.setState(command.getState());
        return event;
    }

    public static MessageResultEvent failure(MessageCommandEvent command, String errorCode, String errorMessage) {
        MessageResultEvent event = base(command);
        event.setSuccess(false);
        event.setMessageId(command.getMessageId());
        event.setTweetId(command.getTweetId());
        event.setContent(command.getContent());
        event.setState(command.getState());
        event.setErrorCode(errorCode);
        event.setErrorMessage(errorMessage);
        return event;
    }

    private static MessageResultEvent base(MessageCommandEvent command) {
        MessageResultEvent event = new MessageResultEvent();
        event.setCorrelationId(command.getCorrelationId());
        event.setOperation(command.getOperation());
        return event;
    }

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
