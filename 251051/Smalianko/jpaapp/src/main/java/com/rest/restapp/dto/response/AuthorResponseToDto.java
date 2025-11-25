package com.rest.restapp.dto.response;

public record AuthorResponseToDto(
        Long id,
        String login,
        String firstname,
        String lastname
) {
}
