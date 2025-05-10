package com.example.distcomp.utils;


import jakarta.validation.ValidationException;

public class ValidationUtils {
    public static void validateString(String value, String fieldName, int min, int max) throws ValidationException {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
        if (value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be blank");
        }
        if (value.length() < min || value.length() > max) {
            throw new ValidationException(fieldName + " must be between " + min + " and " + max + " characters");
        }
    }

    public static void validateNotNegative(long value, String fieldName) throws ValidationException {
        if (value < 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }

    public static void validateNotNull(Object value, String fieldName) throws ValidationException {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
    }
}
