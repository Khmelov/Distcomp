package org.example.newsapi.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class News extends BaseEntity {
    private Long userId; // FK
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    // Many-to-Many имитация через хранение ID маркеров
    private Set<Long> markerIds = new HashSet<>();
}