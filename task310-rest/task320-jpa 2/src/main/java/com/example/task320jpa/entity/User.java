package com.example.task320jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * JPA Entity User
 * Маппинг на таблицу tbl_user в схеме distcomp
 */
@Entity
@Table(name = "tbl_user", schema = "distcomp")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "login", nullable = false, unique = true, length = 64)
    private String login;
    
    @Column(name = "password", nullable = false, length = 128)
    private String password;
    
    @Column(name = "firstname", nullable = false, length = 64)
    private String firstname;
    
    @Column(name = "lastname", nullable = false, length = 64)
    private String lastname;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
