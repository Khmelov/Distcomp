package com.distcomp.errorhandling.exceptions;

import com.distcomp.errorhandling.model.ValidationError;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public class NoteNotFoundException extends BusinessValidationException {
    public NoteNotFoundException(final List<ValidationError> errors) {
        super(errors);
    }
}
