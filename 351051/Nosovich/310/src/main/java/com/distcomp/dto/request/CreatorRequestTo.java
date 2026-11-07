package com.distcomp.dto.request;

/**
 * Incoming representation of a {@link com.distcomp.model.Creator}.
 *
 * <p>{@code id} is optional: it is ignored on creation and required only when
 * the collection-level {@code PUT /api/v1.0/creators} endpoint is used.</p>
 */
public record CreatorRequestTo(
        Long id,
        String login,
        String password,
        String firstname,
        String lastname) {
}
