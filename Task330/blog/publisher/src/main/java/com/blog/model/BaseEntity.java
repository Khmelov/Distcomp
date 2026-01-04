package com.blog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @Column(name = "modified", nullable = false)
    private LocalDateTime modified;

    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modified = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }
}