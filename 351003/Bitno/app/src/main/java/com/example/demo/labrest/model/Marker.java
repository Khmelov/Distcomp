package com.example.demo.labrest.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "tbl_marker", schema = "distcomp")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Marker {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32) private String name;
}