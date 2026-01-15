package com.example.task310.domain;

public record Writer(
    Long id,
    String login,
    String password,
    String firstname,
    String lastname
) {}
