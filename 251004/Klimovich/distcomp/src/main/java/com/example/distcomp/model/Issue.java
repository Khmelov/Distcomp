package com.example.distcomp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_issue")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 64, unique = true, nullable = false)
    @Size(min = 2, max = 64)
    private String title;

    @Column(length = 2048, nullable = false)
    @Size(min = 4, max = 2048)
    private String content;

    @Column(nullable = false)
    private Timestamp created;

    @Column(nullable = false)
    private Timestamp modified;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Writer writer;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "tbl_issue_label",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private List<Label> labels = new ArrayList<>();

}