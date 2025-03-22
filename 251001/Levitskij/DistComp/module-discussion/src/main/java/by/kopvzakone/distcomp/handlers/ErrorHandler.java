package by.kopvzakone.distcomp.handlers;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Console;

public class ErrorHandler {
    @Getter
    enum AppException {
        MethodArgumentNotValidException(HttpStatus.BAD_REQUEST, 1),
        NoSuchMethodException(HttpStatus.NOT_FOUND, 2),
        NotFoundException(HttpStatus.NOT_FOUND, 3),
        ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, 4),
        BadRequestException(HttpStatus.BAD_REQUEST, 5),
        NoSuchElementException(HttpStatus.NOT_FOUND, 6),
        DataIntegrityViolationException(HttpStatus.FORBIDDEN, 7),
        NotFound(HttpStatus.NOT_FOUND, 8),
        FORBIDDEN(HttpStatus.FORBIDDEN, 9),
        RuntimeException(HttpStatus.INTERNAL_SERVER_ERROR, 99);

        private final HttpStatusCode httpStatusCode;
        private final int code;

        AppException(HttpStatusCode status, int code) {
            this.httpStatusCode = status;
            this.code = code;
        }
        int getErrorCode() {
            return httpStatusCode.value() * 100 + code;
        }
        public static AppException resolve(String name){
            try{
                return valueOf(name);
            } catch (Exception e){
                return RuntimeException;
            }
        }
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ControllerAdvice
    public static class AppExceptionHandler{
        @ResponseBody
        @ExceptionHandler(RuntimeException.class)
        public ErrorResponse handle(RuntimeException ex){
            var error = AppException.resolve(ex.getClass().getSimpleName());

            return ErrorResponse
                    .builder(ex, error.getHttpStatusCode(), error.name())
                    .title(ex.getMessage())
                    .property("errorCode", error.getErrorCode())
                    .build();
        }
    }
}
