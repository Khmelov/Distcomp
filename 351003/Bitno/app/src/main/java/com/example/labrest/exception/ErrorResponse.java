package com.example.labrest.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
class ErrorResponse {
    private int errorCode;
    private String errorMessage;
}