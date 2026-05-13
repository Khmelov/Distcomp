package com.example.task330.exception;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor // ОБЯЗАТЕЛЬНО
public class ErrorResponse {
    private String errorMessage;
    private int errorCode;
}