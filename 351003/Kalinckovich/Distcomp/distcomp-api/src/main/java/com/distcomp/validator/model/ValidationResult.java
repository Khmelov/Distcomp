package com.distcomp.validator.model;

import com.distcomp.errorhandling.model.ValidationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ValidationResult(List<ValidationError> errors) {

    public static ValidationResult ok() {
        return new ValidationResult(Collections.emptyList());
    }

    public static ValidationResult of(final String field, final String message) {
        return new ValidationResult(List.of(new ValidationError(field, message)));
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public ValidationResult merge(final ValidationResult other) {
        if (other.isValid()) {
            return this;
        }
        final List<ValidationError> merged = new ArrayList<>(this.errors);

        merged.addAll(other.errors());

        return new ValidationResult(merged);
    }
}