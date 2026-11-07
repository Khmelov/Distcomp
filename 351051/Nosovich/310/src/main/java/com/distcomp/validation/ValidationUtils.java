package com.distcomp.validation;

import com.distcomp.exception.ValidationException;

/**
 * Reusable field checks used by the service layer.
 *
 * <p>Validation lives in the business logic on purpose: a controller only moves
 * data, it never decides whether the data is acceptable.</p>
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    /**
     * Ensures that a mandatory reference (identifier) is present.
     *
     * @param field name of the field, used in the error message
     * @param value value to check
     */
    public static void requireReference(String field, Object value) {
        if (value == null) {
            throw new ValidationException(field, "must be specified");
        }
    }

    /**
     * Ensures that a text field is present and its length fits the allowed range.
     *
     * @param field     name of the field, used in the error message
     * @param value     value to check
     * @param minLength minimal allowed length, inclusive
     * @param maxLength maximal allowed length, inclusive
     */
    public static void requireText(String field, String value, int minLength, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(field, "must be specified");
        }
        if (value.length() < minLength || value.length() > maxLength) {
            throw new ValidationException(field,
                    "length must be between " + minLength + " and " + maxLength + " characters");
        }
    }
}
