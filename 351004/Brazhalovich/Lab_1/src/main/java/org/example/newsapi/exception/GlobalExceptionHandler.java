package org.example.newsapi.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Ошибки "Не найдено" (User, News и т.д.) -> Статус 403, Код 40301
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getMessage(), 40301));
    }

    // 2. Ошибки дубликатов (логин занят) -> Статус 403, Код 40301
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(AlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getMessage(), 40301));
    }

    // 3. Ошибки базы данных (те самые SQL Error: 23505 и 22001 из твоего лога)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(Exception e) {
        // Любая ошибка БД (например, попытка создать новость с несуществующим userId,
        // если проверка в сервисе вдруг пропустила) -> 403
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Integrity violation", 40301));
    }

    // 4. Ошибки валидации (аннотации @Size, @NotBlank) -> Статус 403, Код 40301
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleValidation(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Validation error", 40301));
    }

    // 5. Все остальные непредвиденные ошибки -> 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage(), 50000));
    }
}