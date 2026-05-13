package org.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_editor")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Editor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    private String firstname;
    private String lastname;
}