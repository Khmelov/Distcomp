package com.distcomp.repository;

import com.distcomp.model.Tag;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Storage of {@link Tag} entities.
 */
@Repository
public interface TagRepository extends BaseRepository<Tag> {

    /**
     * @param name tag name to search for
     * @return the tag with this name, if any
     */
    Optional<Tag> findByName(String name);

    /**
     * @param name tag name to check
     * @return {@code true} when the name is already taken
     */
    boolean existsByName(String name);
}
