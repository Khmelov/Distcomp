package com.example.news.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tbl_writer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Writer extends BaseEntity {

    @Column(nullable = false, unique = true, length = 64)
    private String login;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(length = 64)
    private String firstname;

    @Column(length = 64)
    private String lastname;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Article> articles;
}