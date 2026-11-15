package com.example.task310.exception;

// Данные об ошибке, которые возвращаются клиенту.
public class ApiError {

    private String errorMessage;
    private String errorCode;

    // Конструктор без параметров.
    public ApiError() {
    }

    // Конструктор для заполнения сообщения и кода ошибки.
    public ApiError(String errorMessage, String errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    // Методы доступа к полям ошибки.
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}