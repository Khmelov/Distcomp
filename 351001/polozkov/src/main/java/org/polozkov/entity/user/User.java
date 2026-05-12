package org.polozkov.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.polozkov.entity.issue.Issue;
import org.polozkov.other.enums.UserRole;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tbl_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    private String password;

    private String firstname;

    private String lastname;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.CUSTOMER;


    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Issue> issues;
}
