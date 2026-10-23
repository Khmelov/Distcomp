package com.distcomp.repository;

import com.distcomp.model.Creator;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * In-memory storage of {@link Creator} entities.
 */
@Repository
public class CreatorRepository extends InMemoryRepository<Creator> {

    /**
     * @param login login to search for
     * @return the creator registered with this login, if any
     */
    public Optional<Creator> findByLogin(String login) {
        return findAll().stream()
                .filter(creator -> creator.getLogin() != null && creator.getLogin().equals(login))
                .findFirst();
    }
}
