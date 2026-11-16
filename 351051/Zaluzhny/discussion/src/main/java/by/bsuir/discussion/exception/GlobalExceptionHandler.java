package by.bsuir.discussion.exception;

import by.bsuir.discussion.dto.ErrorResponseTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseTo> handleApi(ApiException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(new ErrorResponseTo(exception.getMessage(), exception.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseTo> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return error(HttpStatus.BAD_REQUEST, ErrorCode.TYPE_MISMATCH,
                "Invalid value for parameter '" + exception.getName() + "'");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseTo> handleUnreadable(HttpMessageNotReadableException exception) {
        return error(HttpStatus.BAD_REQUEST, ErrorCode.MALFORMED_JSON, "Malformed JSON request");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseTo> handleMethod(HttpRequestMethodNotSupportedException exception) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, ErrorCode.METHOD_NOT_ALLOWED, "HTTP method is not supported");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseTo> handleMediaType(HttpMediaTypeNotSupportedException exception) {
        return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorCode.UNSUPPORTED_MEDIA,
                "Content-Type must be application/json");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseTo> handleUnexpected(Exception exception) {
        log.error("Unexpected error", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL, "Internal server error");
    }

    private ResponseEntity<ErrorResponseTo> error(HttpStatus status, ErrorCode errorCode, String message) {
        return ResponseEntity.status(status).body(new ErrorResponseTo(message, errorCode.getCode()));
    }
}
