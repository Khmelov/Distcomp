package com.rest.restapp.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tbl_author")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Login is required")
    @Size(min = 3, max = 64, message = "Login length is not valid")
    String login;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password length is not valid")
    String password;

    @NotBlank(message = "Firstname is required")
    @Size(min = 2, max = 64, message = "Firstname length is not valid")
    String firstname;

    @NotBlank(message = "Lastname is required")
    @Size(min = 2, max = 64, message = "Lastname length is not valid")
    String lastname;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    List<Issue> issues;
}