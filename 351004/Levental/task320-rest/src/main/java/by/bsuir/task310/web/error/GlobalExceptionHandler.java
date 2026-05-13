package by.bsuir.task310.web.error;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException exception) {
        String code = String.format("%03d%02d", exception.getHttpStatus(), Integer.parseInt(exception.getCodeSuffix()));
        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ApiError(exception.getMessage(), code));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException exception) {
        FieldError first = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = first == null ? "Validation error" : first.getField() + ": " + first.getDefaultMessage();
        return ResponseEntity.badRequest().body(new ApiError(message, "40001"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception exception) {
        return ResponseEntity.status(500).body(new ApiError("Internal server error", "50001"));
    }
}
