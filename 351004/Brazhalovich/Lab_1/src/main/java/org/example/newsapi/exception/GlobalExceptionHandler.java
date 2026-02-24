package org.example.newsapi.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. БИЗНЕС-ОШИБКИ (Не найдено или Дубликат логина/заголовка) -> 403
    // Объединяем их в один метод, чтобы не было конфликтов

    @ExceptionHandler({NotFoundException.class, AlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handleBusinessError(RuntimeException e) {
        System.out.println(">>> HANDLED BUSINESS ERROR: " + e.getMessage()); // ЛОГ ДЛЯ НАС
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getMessage(), 40301));
    }

        @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleSqlError(Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Database error", 40301));
        }

        @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationError(Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Validation error", 40301));
        }


    // 4. ВСЕ ОСТАЛЬНЫЕ ОШИБКИ -> 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), 50000));
    }
}