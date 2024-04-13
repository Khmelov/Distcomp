package com.example.rv.impl.creator;

public record CreatorRequestTo(
    Long id,
    String login,
    String password,
    String firstname,
    String lastname
){}
