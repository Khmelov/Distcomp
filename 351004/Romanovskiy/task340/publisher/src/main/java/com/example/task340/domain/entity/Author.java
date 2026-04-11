package com.example.task340.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tbl_author")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Author extends BaseEntity {
    @Column(length = 64, unique = true, nullable = false)
    private String login;

    @Column(length = 128, nullable = false)
    private String password;

    @Column(name = "firstname", length = 64)
    private String firstname;

    @Column(name = "lastname", length = 64)
    private String lastname;
}