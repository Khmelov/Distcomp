package org.polozkov.entity.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.polozkov.entity.issue.Issue;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    private Long id;

    private String login;

    private String password;

    private String firstname;

    private String lastname;

    @OneToMany(mappedBy = "user")
    private List<Issue> issues;
}
