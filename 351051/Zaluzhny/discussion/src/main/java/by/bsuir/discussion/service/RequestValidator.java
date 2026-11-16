package by.bsuir.discussion.service;

import by.bsuir.discussion.exception.ApiException;
import by.bsuir.discussion.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RequestValidator {

    private final Validator validator;

    public RequestValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(Object request) {
        if (request == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION, "Request body is required");
        }
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        if (violations.isEmpty()) {
            return;
        }
        String message = violations.stream()
                .sorted(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION, message);
    }
}
