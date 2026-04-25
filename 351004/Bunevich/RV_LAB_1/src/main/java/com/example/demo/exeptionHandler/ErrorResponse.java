package com.example.demo.exeptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private String path;
        private LocalDateTime timestamp;
}
