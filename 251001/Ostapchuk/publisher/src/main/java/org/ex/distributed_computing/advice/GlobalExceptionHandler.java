package org.ex.distributed_computing.advice;

import org.ex.distributed_computing.dto.response.ErrorResponse;
import org.ex.distributed_computing.exception.DuplicateDatabaseValueException;
import org.ex.distributed_computing.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFoundException(NotFoundException e) {
    return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
  }

  @ExceptionHandler(DuplicateDatabaseValueException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorResponse handleDuplicateException(Exception e) {
    return new ErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage());
  }
}
