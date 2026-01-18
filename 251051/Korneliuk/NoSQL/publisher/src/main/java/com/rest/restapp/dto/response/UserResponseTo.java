package com.rest.restapp.dto.response;

public record UserResponseTo(
        Long id,
        String login,
        String firstname,
        String lastname
) {
}
