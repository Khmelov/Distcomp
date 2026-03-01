package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "tbl_mark", schema = "distcomp")
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 32)
    private String name;

    @ManyToMany(mappedBy = "marks")
    private Set<Issue> issues = new HashSet<>();
}
