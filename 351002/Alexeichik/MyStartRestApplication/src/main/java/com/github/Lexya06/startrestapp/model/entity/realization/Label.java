package com.github.Lexya06.startrestapp.model.entity.realization;

import com.github.Lexya06.startrestapp.model.entity.abstraction.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.HashSet;

@Getter
@Setter
@Entity
@Table(name = "labels")
public class Label extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "labels_seq")
    @SequenceGenerator(name = "labels_seq", sequenceName = "labels_seq", allocationSize = 1)
    private Long id;

    @Column(length = 32, unique = true)
    private String name;

    @ManyToMany(mappedBy = "labels", fetch = FetchType.LAZY)
    private Set<Article> articles = new HashSet<>();
}
