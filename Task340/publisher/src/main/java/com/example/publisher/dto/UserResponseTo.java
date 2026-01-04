package com.example.publisher.dto;

public record UserResponseTo(
        Long id,
        String login,
        String firstname,
        String lastname
) {}