package com.example.entitiesapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_writer")
@Getter
@Setter
public class Writer extends BaseEntity {
    @Column(name = "login", unique = true, nullable = false, length = 64)
    private String login;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "firstname", nullable = false, length = 64)  // Изменено с first_name
    private String firstName;

    @Column(name = "lastname", nullable = false, length = 64)   // Изменено с last_name
    private String lastName;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Article> articles = new ArrayList<>();
}