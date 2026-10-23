package com.distcomp.repository;

import com.distcomp.model.Tag;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * In-memory storage of {@link Tag} entities.
 */
@Repository
public class TagRepository extends InMemoryRepository<Tag> {

    /**
     * @param name tag name to search for
     * @return the tag with this name, if any
     */
    public Optional<Tag> findByName(String name) {
        return findAll().stream()
                .filter(tag -> tag.getName() != null && tag.getName().equals(name))
                .findFirst();
    }
}
