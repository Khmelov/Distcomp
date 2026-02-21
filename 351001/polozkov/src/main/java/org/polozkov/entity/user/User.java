package org.polozkov.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.polozkov.entity.issue.Issue;

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

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Issue> issues;
}
