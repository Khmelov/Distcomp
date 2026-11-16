package by.bsuir.rest.kafka;

import by.bsuir.rest.dto.MessageState;
import by.bsuir.rest.dto.response.MessageResponseTo;

import java.util.List;

public class MessageEvent {

    public static final String CREATE = "CREATE";
    public static final String GET = "GET";
    public static final String GET_ALL = "GET_ALL";
    public static final String GET_BY_STORY = "GET_BY_STORY";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";

    private String operation;
    private String correlationId;
    private Long id;
    private Long storyId;
    private String content;
    private MessageState state;
    private List<MessageResponseTo> messages;
    private String errorMessage;
    private Integer errorCode;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
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

    public List<MessageResponseTo> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageResponseTo> messages) {
        this.messages = messages;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
