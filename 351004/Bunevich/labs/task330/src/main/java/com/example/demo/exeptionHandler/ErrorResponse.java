package com.example.demo.exeptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ErrorResponse {
        private String errorMessage;
        private int errorCode;
        public ErrorResponse() {
        }

        // Конструктор с параметрами
        public ErrorResponse(String errorMessage, int errorCode) {
                this.errorMessage = errorMessage;
                this.errorCode = errorCode;
        }

        // Геттеры и сеттеры
        public String getErrorMessage() {
                return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
                this.errorMessage = errorMessage;
        }

        public int getErrorCode() {
                return errorCode;
        }

        public void setErrorCode(int errorCode) {
                this.errorCode = errorCode;
        }
}
