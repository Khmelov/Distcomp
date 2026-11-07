package com.distcomp.dto.response;

/**
 * Outgoing representation of a {@link com.distcomp.model.Tag}.
 */
public record TagResponseTo(
        Long id,
        String name) {
}
