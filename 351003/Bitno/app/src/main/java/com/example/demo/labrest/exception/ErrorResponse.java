package com.example.demo.labrest.exception;

import lombok.*;

@Data @AllArgsConstructor
public class ErrorResponse {
    private int errorCode;
    private String errorMessage;
}