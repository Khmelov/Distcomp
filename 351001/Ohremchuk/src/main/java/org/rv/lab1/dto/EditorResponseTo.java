package org.rv.lab1.dto;

import org.rv.lab1.domain.EditorRole;

public record EditorResponseTo(
        long id,
        String login,
        String firstname,
        String lastname,
        EditorRole role
) {
}

