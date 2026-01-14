package org.example;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "tbl_writer")
public class Writer extends BaseEntity {


    @Column(name = "login", nullable = false, unique = true, length = 64)
    private String login;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "firstname", nullable = false, length = 64)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 64)
    private String lastname;

    @OneToMany(
            mappedBy = "writer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Story> stories = new ArrayList<>();

    // getters / setters

}