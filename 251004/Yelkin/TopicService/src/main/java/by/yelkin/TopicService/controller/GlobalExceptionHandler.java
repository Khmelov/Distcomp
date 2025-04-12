package by.yelkin.TopicService.controller;

import by.yelkin.TopicService.exception.ApiError;
import by.yelkin.TopicService.exception.ApiException;
import jakarta.validation.ConstraintViolationException;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Error> handle(ApiException e) {
        log.error("", e);
        return ResponseEntity.status(e.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(createError(e));
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<Error> validationHandle(Exception e) {
        log.error("", e);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createError(ApiError.ERR_BAD_FORMAT, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handle(Exception e) {
        log.error(e.getClass().getCanonicalName(), e);
        final ApiError err = ApiError.ERR_INTERNAL_ERROR;

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createError(err));
    }

    private Error createError(ApiException e) {
        return Error.builder()
                .timestamp(LocalDateTime.now())
                .code(e.getCode())
                .name(e.getName())
                .displayMessage(e.getDisplayMessage())
                .httpStatus(e.getHttpStatus())
                .build();
    }

    private Error createError(ApiError err, String message) {
        return Error.builder()
                .timestamp(LocalDateTime.now())
                .code(err.getCode())
                .name(err.name())
                .displayMessage(String.format(err.getDisplayMessage(), message))
                .httpStatus(err.getHttpStatus())
                .build();
    }

    private Error createError(ApiError err) {
        return Error.builder()
                .timestamp(LocalDateTime.now())
                .code(err.getCode())
                .name(err.name())
                .displayMessage(err.getDisplayMessage())
                .httpStatus(err.getHttpStatus())
                .build();
    }

    @Builder
    @Getter
    @RequiredArgsConstructor
    public static class Error {
        /**
         * Дата и время ошибки
         */
        private final LocalDateTime timestamp;

        /**
         * Код ошибки по справочнику
         */
        private final String code;

        /**
         * Наименование ошибки по справочнику
         */
        private final String name;

        /**
         * HTTP статус
         */
        private final HttpStatus httpStatus;

        /**
         * Сообщение для пользователя
         */
        private final String displayMessage;
    }
}
