package com.example.distcomp.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_writer")
public class Writer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 64, unique = true ,nullable = false)
    @Size(min = 2, max = 64)
    private String login;

    @Column(length = 128, nullable = false)
    @Size(min = 8, max = 128)
    private String password;

    @Column(length = 64, nullable = false)
    @Size(min = 2, max = 64)
    private String firstname;

    @Column(length = 64, nullable = false)
    @Size(min = 2, max = 64)
    private String lastname;

}
