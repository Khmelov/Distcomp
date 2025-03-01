package com.dc.anotherone.model.blo;

import jakarta.persistence.*;
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
@Table(name="tbl_author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 64, nullable = false)
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
