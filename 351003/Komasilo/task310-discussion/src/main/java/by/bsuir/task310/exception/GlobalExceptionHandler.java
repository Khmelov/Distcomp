package by.bsuir.task310.exception;

import by.bsuir.task310.dto.ErrorResponseTo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseTo handleNotFound(EntityNotFoundException ex) {
        return new ErrorResponseTo(ex.getMessage(), "40401");
    }

    @ExceptionHandler(DuplicateException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseTo handleDuplicate(DuplicateException ex) {
        return new ErrorResponseTo(ex.getMessage(), "40301");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseTo handleValidation(MethodArgumentNotValidException ex) {
        return new ErrorResponseTo("Validation error", "40001");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseTo handleOther(Exception ex) {
        return new ErrorResponseTo(ex.getMessage(), "40001");
    }
}