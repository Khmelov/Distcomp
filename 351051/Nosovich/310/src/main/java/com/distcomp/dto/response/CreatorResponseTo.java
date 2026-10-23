package com.distcomp.dto.response;

/**
 * Outgoing representation of a {@link com.distcomp.model.Creator}.
 */
public record CreatorResponseTo(
        Long id,
        String login,
        String password,
        String firstname,
        String lastname) {
}
