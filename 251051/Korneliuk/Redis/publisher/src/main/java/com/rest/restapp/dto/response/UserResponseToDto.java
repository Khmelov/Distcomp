package com.rest.restapp.dto.response;

public record UserResponseToDto(
        Long id,
        String login,
        String firstname,
        String lastname
) {
}
