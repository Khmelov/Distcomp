package by.bsuir.publisher.exceptions;

import lombok.Getter;

@Getter
public class ServiceException extends Exception {
    private final ErrorDto errorDto;

    public ServiceException(ErrorDto errorDto) {
        this.errorDto = errorDto;
    }
}
