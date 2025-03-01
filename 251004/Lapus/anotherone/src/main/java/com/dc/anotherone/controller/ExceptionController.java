package com.dc.anotherone.controller;

import com.dc.anotherone.exception.ServiceException;
import com.dc.anotherone.model.dto.AuthorResponseTo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionController {

/*    @ExceptionHandler(value = Exception.class)
    public ErrorResponse commonException(@RequestBody Exception ex) {
        System.out.println(ex.getMessage());
        return ErrorResponse.builder(ex, HttpStatus.NOT_ACCEPTABLE, "Author").build();
    }*/

/*    @ExceptionHandler(value = RuntimeException.class)
    public ErrorResponse find(@RequestBody Exception ex) {
        System.out.println(ex.getMessage());
        return ErrorResponse.builder(ex, HttpStatus.NOT_ACCEPTABLE, "Author").build();
    }*/

    @ExceptionHandler(value = ServiceException.class)
    public ResponseEntity<ExceptionObject> commonException2(@RequestBody ServiceException ex) {
        return ResponseEntity.status(ex.getErrorCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ExceptionObject(ex.getErrorCode(), ex.getMessage(), ex.getMessage()));
        //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public record ExceptionObject(int code, String status, String message){}

}
