package com.example.Task310.bean;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "tbl_editor", schema = "distcomp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Editor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String login;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(nullable = false, length = 64)
    private String firstname; // Обязательно для тестов

    @Column(nullable = false, length = 64)
    private String lastname;  // Обязательно для тестов

    @OneToMany(mappedBy = "editor", cascade = CascadeType.ALL)
    private List<Story> stories;
}