package com.distcomp.repository;

import com.distcomp.model.Creator;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Storage of {@link Creator} entities.
 */
@Repository
public interface CreatorRepository extends BaseRepository<Creator> {

    /**
     * @param login login to search for
     * @return the creator registered with this login, if any
     */
    Optional<Creator> findByLogin(String login);

    /**
     * @param login login to check
     * @return {@code true} when the login is already taken
     */
    boolean existsByLogin(String login);
}
