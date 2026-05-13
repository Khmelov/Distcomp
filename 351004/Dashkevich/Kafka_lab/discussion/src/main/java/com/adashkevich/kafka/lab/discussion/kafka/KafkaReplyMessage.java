package com.adashkevich.kafka.lab.discussion.kafka;

import com.adashkevich.kafka.lab.discussion.dto.MessageResponseTo;

import java.util.List;

public class KafkaReplyMessage {
    private String correlationId;
    private boolean success;
    private int httpStatus;
    private String errorCode;
    private String errorMessage;
    private MessageResponseTo message;
    private List<MessageResponseTo> messages;

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public int getHttpStatus() { return httpStatus; }
    public void setHttpStatus(int httpStatus) { this.httpStatus = httpStatus; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public MessageResponseTo getMessage() { return message; }
    public void setMessage(MessageResponseTo message) { this.message = message; }
    public List<MessageResponseTo> getMessages() { return messages; }
    public void setMessages(List<MessageResponseTo> messages) { this.messages = messages; }
}
