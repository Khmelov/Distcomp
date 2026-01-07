package com.group310971.gormash.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_editor")
public class Editor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 2, max = 64)
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Size(min = 8, max = 128)
    @Column(name = "password", nullable = false)
    private String password;

    @Size(min = 2, max = 64)
    @Column(name = "firstname")
    private String firstname;

    @Size(min = 2, max = 64)
    @Column(name = "lastname")
    private String lastname;
}