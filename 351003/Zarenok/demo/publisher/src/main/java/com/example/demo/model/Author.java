package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tbl_author", schema = "dictcomp")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", nullable = false, length = 64, unique = true)
    private String login;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "firstname", length = 64, nullable = false)
    private String firstname;

    @Column(name = "lastname", length = 64, nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> issues = new ArrayList<>();

}
