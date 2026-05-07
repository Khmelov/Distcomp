package com.example.news.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tbl_marker")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marker extends BaseEntity {

    @Column(nullable = false, unique = true, length = 32)
    private String name;

    @ManyToMany(mappedBy = "markers")
    private List<Article> articles;
}