package com.example.task320jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA Entity Mark
 * Маппинг на таблицу tbl_mark в схеме distcomp
 */
@Entity
@Table(name = "tbl_mark", schema = "distcomp")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mark {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, unique = true, length = 32)
    private String name;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Many-to-Many связь с Tweet (обратная сторона)
     */
    @ManyToMany(mappedBy = "marks", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Tweet> tweets = new HashSet<>();
}
