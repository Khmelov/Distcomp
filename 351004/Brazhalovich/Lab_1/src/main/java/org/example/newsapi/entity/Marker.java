package org.example.newsapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_marker")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marker {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "marker_seq_gen")
    @SequenceGenerator(name = "marker_seq_gen", sequenceName = "marker_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false, length = 32)
    private String name;
}