package org.polozkov.entity.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.polozkov.entity.issue.Issue;

import java.util.List;

@Entity
@Getter
@Setter
public class User {

    @Id
    private Long id;

    private String login;

    private String password;

    private String firstName;

    private String lastName;

    @OneToMany(mappedBy = "user")
    private List<Issue> issues;
}
