package com.example.publisher.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tbl_author", schema = "distcomp")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String login;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(nullable = false, length = 64)
    private String firstname;

    @Column(nullable = false, length = 64)
    private String lastname;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Article> articles = new ArrayList<>();
}