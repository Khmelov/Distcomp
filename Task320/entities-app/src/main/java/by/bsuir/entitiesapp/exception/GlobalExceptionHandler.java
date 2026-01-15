package by.bsuir.entitiesapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException ex) {
        ApiError error = new ApiError();
        error.errorMessage = ex.getMessage();
        error.errorCode = ex.errorCode;
        return error;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(BadRequestException ex) {
        ApiError error = new ApiError();
        error.errorMessage = ex.getMessage();
        error.errorCode = ex.errorCode;
        return error;
    }

    @ExceptionHandler(BusinessLogicException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleBusinessLogic(BusinessLogicException ex) {
        ApiError error = new ApiError();
        error.errorMessage = ex.getMessage();
        error.errorCode = ex.errorCode;
        return error;
    }
}
