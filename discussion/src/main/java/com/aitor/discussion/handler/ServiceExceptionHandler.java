package com.aitor.discussion.handler;

import com.aitor.publisher.exception.ServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    ResponseEntity<Object> handleCustomConflict(ServiceException ex, WebRequest request) {
        return super.handleExceptionInternal(ex,
                new ResponseEntity<>((Object) new Error(ex.getMessage()), new HttpHeaders(), ex.getStatusCode()),
                new HttpHeaders(), ex.getStatusCode(), request);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        return super.handleExceptionInternal(ex, (Object) new Error("Unexpected exception" + ex.toString()),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
