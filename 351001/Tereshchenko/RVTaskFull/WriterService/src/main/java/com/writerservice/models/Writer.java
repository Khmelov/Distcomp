package com.writerservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Entity
@Table(name = "tbl_writer")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Writer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "login", unique = true)
    @Size(min = 2, max = 64)
    public String login;

    @Column(name = "password")
    @Size(min = 8, max = 128)
    public String password;

    @Column(name = "firstname")
    @Size(min = 2, max = 64)
//    @Pattern(regexp = "[A-Za-z]+")
    public String firstname;

    @Column(name = "lastname")
    @Size(min = 2, max = 64)
//    @Pattern(regexp = "[A-Za-z]+")
    public String lastname;

}
