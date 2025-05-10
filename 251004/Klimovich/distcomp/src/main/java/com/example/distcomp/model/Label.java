package com.example.distcomp.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_label")
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 32, unique = true, nullable = false)
    @Size(min = 2, max = 32)
    private String name;


    @ManyToMany(mappedBy = "labels")
    private Set<Issue> issues = new HashSet<>();


}
