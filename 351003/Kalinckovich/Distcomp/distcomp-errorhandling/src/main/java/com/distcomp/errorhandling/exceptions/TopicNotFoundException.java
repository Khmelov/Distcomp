package com.distcomp.errorhandling.exceptions;

import com.distcomp.errorhandling.model.ValidationError;

import java.util.List;

public class TopicNotFoundException extends BusinessValidationException {
    public TopicNotFoundException(final List<ValidationError> errors) {
        super(errors);
    }
}
