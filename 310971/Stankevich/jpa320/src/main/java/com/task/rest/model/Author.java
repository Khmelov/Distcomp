package com.task.rest.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tbl_author")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "tweets")
@NoArgsConstructor
@AllArgsConstructor
public class Author extends BaseEntity {

    @Column(nullable = false, length = 64, unique = true)
    private String login;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(nullable = false, length = 64)
    private String firstname;

    @Column(nullable = false, length = 64)
    private String lastname;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tweet> tweets;
}