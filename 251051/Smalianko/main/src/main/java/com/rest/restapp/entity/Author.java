package com.rest.restapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "authors")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Author {

    @Id
    @GeneratedValue
    Long id;

    @NotBlank(message = "Login is required")
    @Size(min = 3, max = 64, message = "Login length is not valid")
    String login;

    @NotBlank(message = "Password is required")
    @Size(max = 128, message = "Password must not exceed 128 characters")
    String password;

    @NotBlank(message = "Firstname is required")
    @Size(max = 64, message = "Firstname must not exceed 64 characters")
    String firstname;

    @NotBlank(message = "Lastname is required")
    @Size(max = 64, message = "Lastname must not exceed 64 characters")
    String lastname;
}