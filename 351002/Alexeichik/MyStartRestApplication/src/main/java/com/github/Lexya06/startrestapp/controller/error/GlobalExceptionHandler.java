package com.github.Lexya06.startrestapp.controller.error;
import com.github.Lexya06.startrestapp.service.customexception.MyEntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private ProblemDetail finalizeError(ErrorDescription error, String errorMessage){
        int errorCode = error.getErrorCode();
        ProblemDetail body = ProblemDetail.forStatusAndDetail(error.getStatus(), errorMessage);
        body.setProperty("errorCode", errorCode);
        return body;
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            @NonNull TypeMismatchException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        Object[] args = new Object[]{ex.getPropertyName(), ex.getValue()};
        String propertyName = String.valueOf(args[0]);
        String errorMessage = "Failed to convert [" + propertyName + "] with value: [" + args[1] + "]";
        return this.handleExceptionInternal(ex, finalizeError(ErrorDescription.TYPE_MISMATCH, errorMessage), headers, ErrorDescription.TYPE_MISMATCH.getStatus(), request);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
           @NonNull WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        String errorMessage = allErrors.stream().map(error ->{
            if (error instanceof FieldError fieldError) {
                return String.format("Field [%s] is invalid, message: [%s], rejected value: [%s]", 
                        fieldError.getField(), fieldError.getDefaultMessage(), fieldError.getRejectedValue());
            } else {
                return String.format("Object [%s] is invalid, message: [%s]", error.getObjectName(), error.getDefaultMessage());
            }
        }).collect(Collectors.joining("; \n"));
        return super.handleExceptionInternal(ex, finalizeError(ErrorDescription.BAD_REQUEST_BODY, errorMessage), headers, status, request);
    }

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            @NonNull HttpRequestMethodNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        String errorMessage = "Method " + ex.getMethod() + " not supported";
        return super.handleExceptionInternal(ex, finalizeError(ErrorDescription.REQUEST_METHOD_NOT_SUPPORTED, errorMessage), headers, status, request);
    }

    // for incorrect request body deserialization
    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        String errorMessage = "Message " + ex.getMessage() + " cannot be read";
        return super.handleExceptionInternal(ex, finalizeError(ErrorDescription.MESSAGE_NOT_READABLE, errorMessage), headers, status, request);
    }


    @Override
    public ResponseEntity<Object> handleNoResourceFoundException(
            @NonNull NoResourceFoundException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        String errorMessage = "Resource with path " + ex.getResourcePath() + " not found";
        return super.handleExceptionInternal(ex, finalizeError(ErrorDescription.ENTITY_NOT_FOUND, errorMessage), headers, status, request);
    }

    @ExceptionHandler(MyEntityNotFoundException.class)
    public ResponseEntity<Object> handleMyEntityNotFound(
            @NonNull MyEntityNotFoundException ex
    )
    {
        return ResponseEntity.status(ErrorDescription.ENTITY_NOT_FOUND.getStatus()) .body(finalizeError(ErrorDescription.ENTITY_NOT_FOUND, ex.getMessage()));
    }



}
