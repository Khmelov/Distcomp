package com.example.task320.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_marker")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Marker extends BaseEntity {

    @Column(length = 32, nullable = false, unique = true)
    private String name;

    // Указываем, что связь многие-ко-многим управляется полем markers в классе Tweet
    @ManyToMany(mappedBy = "markers")
    @Builder.Default
    @ToString.Exclude // Исключаем из toString, чтобы не было бесконечного цикла
    private List<Tweet> tweets = new ArrayList<>();
}