package by.bsuir.discussion.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = {NoEntityExistsException.class, EntityExistsException.class})
    protected ResponseEntity<ErrorDto> handleEntityExceptions(Exception e) {
        return new ResponseEntity<>(ErrorDto.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value() + "00")
                .errorMessage(e.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NullPointerException.class})
    protected ResponseEntity<ErrorDto> handleNullPointerException() {
        return new ResponseEntity<>(ErrorDto.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value() + "00")
                .errorMessage(Comments.NullPointerException)
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    protected ResponseEntity<ErrorDto> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>(ErrorDto.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value() + "00")
                .errorMessage(e.getMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    protected ResponseEntity<ErrorDto> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ResponseEntity<>(ErrorDto.builder()
                .errorCode(HttpStatus.FORBIDDEN.value() + "00")
                .errorMessage(e.getMessage())
                .build(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorDto> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ErrorDto.builder()
                .errorMessage(msg)
                .errorCode("40001")
                .build());
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ErrorDto> handleAuth(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorDto.builder()
                .errorMessage(e.getMessage())
                .errorCode("40100")
                .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorDto> handleDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorDto.builder()
                .errorMessage(e.getMessage())
                .errorCode("40300")
                .build());
    }
}
