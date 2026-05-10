package com.example.demo.labrest.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "tbl_creator", schema = "distcomp")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Creator {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64) private String login;
    @Column(nullable = false, length = 128) private String password;
    @Column(nullable = false, length = 64) private String firstname;
    @Column(nullable = false, length = 64) private String lastname;
}