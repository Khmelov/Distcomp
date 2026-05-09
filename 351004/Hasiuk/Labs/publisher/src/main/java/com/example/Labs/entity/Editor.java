package com.example.Labs.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "tbl_editor")
@Getter @Setter
public class Editor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 64)
    private String login;
    @Column(nullable = false, length = 128)
    private String password;
    @Column(nullable = false, length = 64)
    private String firstname;
    @Column(nullable = false, length = 64)
    private String lastname;
    @Column(nullable = false, length = 20)
    private String role = "CUSTOMER";
}