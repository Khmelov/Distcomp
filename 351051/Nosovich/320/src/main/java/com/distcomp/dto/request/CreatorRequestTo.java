package com.distcomp.dto.request;

/**
 * Incoming representation of a {@link com.distcomp.model.Creator}.
 *
 * <p>{@code id} is optional: it is ignored on creation and used by the
 * collection-level {@code PUT /api/v1.0/creators} endpoint.</p>
 */
public record CreatorRequestTo(
        Long id,
        String login,
        String password,
        String firstname,
        String lastname) {
}
