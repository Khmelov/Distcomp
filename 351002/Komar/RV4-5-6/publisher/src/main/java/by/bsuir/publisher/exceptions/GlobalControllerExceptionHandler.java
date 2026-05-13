package by.bsuir.publisher.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    protected ResponseEntity<ErrorDto> handleServiceException(ServiceException e) {
        ErrorDto dto = e.getErrorDto() != null ? e.getErrorDto() : ErrorDto.builder().
                code(HttpStatus.BAD_REQUEST.value() + "00").
                comment(e.getMessage()).
                build();
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { NoEntityExistsException.class, EntityExistsException.class })
    protected ResponseEntity<ErrorDto> handleEntityExceptions(Exception e) {
        return new ResponseEntity<>(ErrorDto.builder().
                code(HttpStatus.BAD_REQUEST.value() + "00").
                comment(e.getMessage()).
                build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { NullPointerException.class })
    protected ResponseEntity<ErrorDto> handleNullPointerException() {
        return new ResponseEntity<>(ErrorDto.builder().
                code(HttpStatus.BAD_REQUEST.value() + "00").
                comment(Comments.NullPointerException).
                build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    protected ResponseEntity<ErrorDto> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>(ErrorDto.builder().
                code(HttpStatus.BAD_REQUEST.value() + "00").
                comment(e.getMessage()).
                build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { DataIntegrityViolationException.class })
    protected ResponseEntity<ErrorDto> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ResponseEntity<>(ErrorDto.builder().
                code(HttpStatus.FORBIDDEN.value() + "00").
                comment(e.getMessage()).
                build(), HttpStatus.FORBIDDEN);
    }
}
