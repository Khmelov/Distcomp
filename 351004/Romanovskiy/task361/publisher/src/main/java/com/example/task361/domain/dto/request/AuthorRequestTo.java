package com.example.task361.domain.dto.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.example.task361.security.Role;
import com.fasterxml.jackson.annotation.JsonAlias;

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
    @JsonAlias("firstName")
    private String firstname;

    @NotNull
    @Size(min = 2, max = 64)
    @JsonAlias("lastName")
    private String lastname;

    // Для регистрации в /api/v2.0/authors (если не указано — будет CUSTOMER)
    private Role role;
}
