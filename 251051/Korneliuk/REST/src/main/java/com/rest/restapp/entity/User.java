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
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue
    Long id;

    @NotBlank(message = "Login is required")
    @Size(min = 2, max = 64, message = "Login (2...64 chars)")
    String login;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password (8...2048 chars)")
    String password;

    @NotBlank(message = "Firstname is required")
    @Size(min = 2, max = 64, message = "Firstname (2...64 chars)")
    String firstname;

    @NotBlank(message = "Lastname is required")
    @Size(min = 2, max = 64, message = "Lastname (2...64 chars)")
    String lastname;
}