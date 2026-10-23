package com.distcomp.repository;

import com.distcomp.model.Issue;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Storage of {@link Issue} entities.
 */
@Repository
public interface IssueRepository extends BaseRepository<Issue> {

    /**
     * @param title title to check
     * @return {@code true} when an issue with this title already exists
     */
    boolean existsByTitle(String title);

    /**
     * @param title title to search for
     * @return the issue with this title, if any
     */
    Optional<Issue> findByTitle(String title);

    /**
     * @param creatorId identifier of the author
     * @return every issue written by this creator
     */
    List<Issue> findByCreatorId(Long creatorId);

    /**
     * @param tagId identifier of the tag
     * @return every issue labelled with this tag
     */
    List<Issue> findByTagsId(Long tagId);

    /**
     * @param tagId identifier of the tag
     * @return number of issues still labelled with this tag
     */
    long countByTagsId(Long tagId);

    /**
     * Removes every issue of the given author.
     *
     * @param creatorId identifier of the author
     */
    void deleteByCreatorId(Long creatorId);
}
