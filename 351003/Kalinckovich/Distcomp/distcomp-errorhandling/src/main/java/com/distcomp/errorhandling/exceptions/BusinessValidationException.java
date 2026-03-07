package com.distcomp.errorhandling.exceptions;

import com.distcomp.errorhandling.model.ValidationError;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class BusinessValidationException extends RuntimeException {

    private static final String DEFAULT_VALIDATION_FAILED_MESSAGE = "Validation failed";
    private final List<ValidationError> errors;

    public BusinessValidationException(final String message) {
        super(message);
        this.errors = Collections.singletonList(
                ValidationError.builder().field("global").message(message).build()
        );
    }

    public BusinessValidationException(final List<ValidationError> errors) {
        super(DEFAULT_VALIDATION_FAILED_MESSAGE);
        this.errors = errors;
    }
}