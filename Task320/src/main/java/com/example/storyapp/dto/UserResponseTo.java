package com.example.storyapp.dto;

public record UserResponseTo(
        Long id,
        String login,
        String firstname,
        String lastname
) {}