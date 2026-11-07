package com.distcomp.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Base class of every persisted entity: owns the surrogate primary key only.
 *
 * <p>Identity comparison is left to the JPA provider on purpose: mutable
 * entities with database generated identifiers must not implement
 * {@code equals} / {@code hashCode} on a not-yet-assigned identifier.</p>
 */
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
