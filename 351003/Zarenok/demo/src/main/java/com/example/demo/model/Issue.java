package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "tbl_issue", schema = "distcomp")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 64)
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "created")
    private ZonedDateTime created;

    @Column(name = "modified")
    private ZonedDateTime modified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tbl_issue_mark",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "mark_id")
    )
    private Set<Mark> marks = new HashSet<>();
}
