package com.distcomp.common.notice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * Life-cycle state of a notice.
 *
 * <p>The assignment spells the negative state as {@code DELCINE}. The enum uses
 * the correct spelling {@code DECLINE}; the typo is accepted on input so that a
 * client following the assignment text is not rejected.</p>
 */
public enum NoticeState {

    /** Created but not moderated yet. */
    PENDING,

    /** Accepted by the automatic moderator. */
    APPROVE,

    /** Rejected by the automatic moderator. */
    DECLINE;

    @JsonCreator
    public static NoticeState fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if ("DELCINE".equals(normalized)) {
            return DECLINE;
        }
        return valueOf(normalized);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
