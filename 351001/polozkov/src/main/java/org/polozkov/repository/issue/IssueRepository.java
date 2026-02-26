package org.polozkov.repository.issue;

import org.polozkov.entity.issue.Issue;
import org.polozkov.exception.BadRequestException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    default Issue byId(Long id) {
        return findById(id).orElseThrow(() -> new BadRequestException("Issue with id "  + id + " does not exists "));
    }

    Optional<Issue> findByTitle(String title);

}