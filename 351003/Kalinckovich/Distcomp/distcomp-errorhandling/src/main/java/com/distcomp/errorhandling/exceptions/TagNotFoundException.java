package com.distcomp.errorhandling.exceptions;

import com.distcomp.errorhandling.model.ValidationError;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public class TagNotFoundException extends BusinessValidationException {
    public TagNotFoundException(final List<ValidationError> errors) {
        super(errors);
    }
}
