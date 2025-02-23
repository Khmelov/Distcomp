package by.kapinskiy.Distcomp.utils.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomInformativeException {
    public NotFoundException(String message) {
        super(message, 40401, HttpStatus.NOT_FOUND);
    }
}
