package com.distcomp.validator.model;

import java.util.Collections;
import java.util.Map;

public record ValidationArgs(Long id, Map<String, Object> extras) {

    public static ValidationArgs empty() {
        return new ValidationArgs(null, Collections.emptyMap());
    }

    public static ValidationArgs withId(final Long id) {
        return new ValidationArgs(id, Collections.emptyMap());
    }

    public static ValidationArgs of(final Long id, final Map<String, Object> extras) {
        return new ValidationArgs(id, extras);
    }
}
