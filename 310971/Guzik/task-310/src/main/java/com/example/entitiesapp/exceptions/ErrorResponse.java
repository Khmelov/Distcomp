package com.example.entitiesapp.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private int errorCode;
    private String errorMessage;
    private LocalDateTime timestamp;

    public ErrorResponse(int errorCode, String errorMessage, LocalDateTime timestamp) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }
}