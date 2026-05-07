package com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization;

import com.github.Lexya06.startrestapp.publisher.impl.model.entity.abstraction.BaseEntity;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tbl_user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    private Long id;

    @Column (unique = true, length = 64, nullable = false)
    private String login;

    @Column (length = 128, nullable = false)
    private String password;

    @Column (length = 64, nullable = false)
    private String firstname;

    @Column (length = 64, nullable = false)
    private String lastname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Article> articles = new HashSet<>();
}
