package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Обработка валидации (Тест №2) - Ожидаемый код 40001
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        String message = "Validation failed";
        if (ex.getBindingResult().getFieldError() != null) {
            message = ex.getBindingResult().getFieldError().getDefaultMessage();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "errorMessage", message,
                "errorCode", "40001"
        ));
    }

    // 2. Обработка ResponseStatusException (Тест №3 на 403 и поиск постов на 404)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleStatusException(ResponseStatusException ex) {
        String message = ex.getReason() != null ? ex.getReason() : "Error occurred";

        String errorCode = String.valueOf(ex.getStatusCode().value());
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) errorCode = "40401";

        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "errorMessage", message,
                "errorCode", errorCode
        ));
    }

    // 3. Обработка кастомных ошибок поиска
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "errorMessage", ex.getMessage(),
                "errorCode", "40401"
        ));
    }

    // 4. Ловушка для всех остальных ошибок
    // МЕТОД ОСТАВЛЯЕМ ОДИН
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {
        ex.printStackTrace(); // Чтобы ты видел реальную ошибку в консоли
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "errorMessage", "Internal server error",
                "errorCode", "50000"
        ));
    }
}