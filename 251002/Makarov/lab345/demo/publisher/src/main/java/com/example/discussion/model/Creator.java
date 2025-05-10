package com.example.discussion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "tbl_creator")
public class Creator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "login", nullable = false, length = 64)
    @Size(min = 2, max = 64)
    private String login;
    @Column(name = "password", nullable = false, length = 128)
    private String password;
    @Column(name = "firstname", nullable = false, length = 64)
    private String firstname;
    @Column(name = "lastname", nullable = false, length = 64)
    private String lastname;

    @OneToMany(mappedBy = "creator")
    private List<Article> articles;
}