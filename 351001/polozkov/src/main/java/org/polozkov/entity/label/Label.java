package org.polozkov.entity.label;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.polozkov.entity.issue.Issue;

import java.util.List;

@Entity
@Getter
@Setter
public class Label {
    @Id
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable( name = "label_issue",
            joinColumns = @JoinColumn(name = "label_id"),
            inverseJoinColumns = @JoinColumn(name = "issue_id")

    )
    private List<Issue> issues;
}
