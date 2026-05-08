package by.liza.app.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseException> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseException(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ErrorResponseException> handleDuplicate(DuplicateEntityException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseException(ex.getMessage(), ex.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseException> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseException(message, 40001));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseException> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseException(message, 40002));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseException> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = extractConstraintMessage(ex);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseException(message, 40301));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseException> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseException("Internal server error: " + ex.getMessage(), 50001));
    }

    private String extractConstraintMessage(DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause().getMessage();
        if (msg != null && msg.contains("uq_writer_login"))  return "Writer with this login already exists";
        if (msg != null && msg.contains("uq_article_title")) return "Article with this title already exists";
        if (msg != null && msg.contains("uq_mark_name"))     return "Mark with this name already exists";
        return "Data integrity violation";
    }
}