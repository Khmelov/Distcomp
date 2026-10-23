package com.distcomp.dto.request;

/**
 * Incoming representation of a {@link com.distcomp.model.Tag}.
 */
public record TagRequestTo(
        Long id,
        String name) {
}
