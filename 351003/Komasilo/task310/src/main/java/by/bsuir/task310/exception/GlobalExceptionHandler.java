package by.bsuir.task310.exception;

import by.bsuir.task310.dto.ErrorResponseTo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseTo handleNotFound(EntityNotFoundException ex) {
        return new ErrorResponseTo(ex.getMessage(), "40401");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseTo handleOther(Exception ex) {
        return new ErrorResponseTo(ex.getMessage(), "40401");
    }
}