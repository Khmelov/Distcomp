package com.example.app.dto;

public record AuthorResponseDTO(
        Long id,
        String login,
        String password,
        String firstname,
        String lastname
) {}