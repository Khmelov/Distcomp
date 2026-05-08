package com.example.task340.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}