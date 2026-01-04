package com.example.entitiesapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_sticker")
@Getter
@Setter
public class Sticker extends BaseEntity {
    @Column(name = "name", unique = true, nullable = false, length = 32)
    private String name;

    @ManyToMany(mappedBy = "stickers")
    private Set<Article> articles = new HashSet<>();
}