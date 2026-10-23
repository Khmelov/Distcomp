package com.distcomp.model;

import java.util.Objects;

/**
 * Base class for every persisted-in-memory entity.
 *
 * <p>Holds the surrogate key only: identifier generation belongs to the storage
 * layer, so the domain objects stay free of persistence details.</p>
 */
public abstract class AbstractEntity {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Default identity of an entity is its class plus its identifier. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass() || id == null) {
            return false;
        }
        return Objects.equals(id, ((AbstractEntity) other).id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hash(getClass(), id);
    }
}
