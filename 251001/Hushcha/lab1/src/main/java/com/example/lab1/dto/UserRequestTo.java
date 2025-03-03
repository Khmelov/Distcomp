package com.example.lab1.dto;

import jakarta.validation.constraints.Size;

public record UserRequestTo(
        Long id,
        @Size(min = 2, max = 64) String login,
        String password,

        String firstname,

        String lastname
) {

}
