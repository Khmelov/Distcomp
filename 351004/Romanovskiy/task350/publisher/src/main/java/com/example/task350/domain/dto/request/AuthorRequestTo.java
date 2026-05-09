package com.example.task350.domain.dto.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequestTo {
    private Long id; // Может быть null при создании

    @NotNull
    @Size(min = 2, max = 64)
    private String login;

    @NotNull
    @Size(min = 8, max = 128)
    private String password;

    @NotNull
    @Size(min = 2, max = 64)
    private String firstname;

    @NotNull
    @Size(min = 2, max = 64)
    private String lastname;
}