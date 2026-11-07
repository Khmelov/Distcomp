package com.distcomp.dto.filter;

/**
 * Optional filtering criteria of the creator collection.
 *
 * @param login fragment of the login, matched case-insensitively
 */
public record CreatorFilter(String login) {

    /** @return a filter that accepts every creator */
    public static CreatorFilter empty() {
        return new CreatorFilter(null);
    }
}
