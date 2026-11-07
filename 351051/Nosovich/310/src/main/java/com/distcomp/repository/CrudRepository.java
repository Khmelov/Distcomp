package com.distcomp.repository;

import com.distcomp.model.AbstractEntity;

import java.util.List;
import java.util.Optional;

/**
 * Storage-independent contract for the CRUD operations of a single entity type.
 *
 * <p>The interface is intentionally generic: a future JDBC/JPA implementation
 * replaces the in-memory one without any change in the service layer.</p>
 *
 * @param <T> entity type handled by this repository
 */
public interface CrudRepository<T extends AbstractEntity> {

    /**
     * Stores a new entity and assigns its identifier.
     *
     * @param entity entity to store, must not be {@code null}
     * @return the stored entity with a generated identifier
     */
    T save(T entity);

    /**
     * Replaces an already stored entity.
     *
     * @param entity entity to replace, its identifier must be set
     * @return the stored entity
     * @throws IllegalArgumentException if the entity has no identifier
     */
    T update(T entity);

    /**
     * Looks up an entity by its identifier.
     *
     * @param id identifier to search for
     * @return the entity or an empty optional when it does not exist
     */
    Optional<T> findById(Long id);

    /**
     * @return every stored entity ordered by identifier
     */
    List<T> findAll();

    /**
     * @param id identifier to check
     * @return {@code true} when an entity with this identifier exists
     */
    boolean existsById(Long id);

    /**
     * Removes an entity.
     *
     * @param id identifier of the entity to remove
     * @return the removed entity or an empty optional when it did not exist
     */
    Optional<T> deleteById(Long id);
}
