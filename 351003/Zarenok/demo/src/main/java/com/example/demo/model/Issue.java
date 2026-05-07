package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "issues")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private ZonedDateTime created;
    private ZonedDateTime modified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "issue_marks",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "mark_id")
    )
    private Set<Mark> marks = new HashSet<>();
}
