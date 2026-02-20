package com.distcomp.errorhandling.exceptions;

import com.distcomp.errorhandling.model.ValidationError;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public class UserNotFoundException extends BusinessValidationException {
    public UserNotFoundException(final List<ValidationError> errors) {
        super(errors);
    }
}
