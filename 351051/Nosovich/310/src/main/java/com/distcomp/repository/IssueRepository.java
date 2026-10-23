package com.distcomp.repository;

import com.distcomp.model.Issue;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * In-memory storage of {@link Issue} entities.
 */
@Repository
public class IssueRepository extends InMemoryRepository<Issue> {

    /**
     * @param creatorId identifier of the author
     * @return every issue written by this creator
     */
    public List<Issue> findByCreatorId(Long creatorId) {
        return findAll().stream()
                .filter(issue -> creatorId != null && creatorId.equals(issue.getCreatorId()))
                .toList();
    }

    /**
     * @param tagId identifier of the tag
     * @return every issue labelled with this tag
     */
    public List<Issue> findByTagId(Long tagId) {
        return findAll().stream()
                .filter(issue -> issue.getTagIds() != null && issue.getTagIds().contains(tagId))
                .toList();
    }
}
