package com.blog.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private int errorCode;
    private String errorMessage;
    private LocalDateTime timestamp;
    private Map<String, String> errors;

    // Конструкторы
    public ErrorResponse() {}

    public ErrorResponse(int errorCode, String errorMessage, LocalDateTime timestamp) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    public ErrorResponse(int errorCode, String errorMessage, LocalDateTime timestamp, Map<String, String> errors) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    // Геттеры и сеттеры
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}