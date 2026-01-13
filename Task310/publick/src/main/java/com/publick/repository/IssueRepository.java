package com.publick.repository;

import com.publick.entity.Issue;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class IssueRepository extends InMemoryCrudRepository<Issue, Long> {

    @Override
    protected Long getId(Issue entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Issue entity, Long id) {
        entity.setId(id);
    }

    public List<Issue> findByAuthorId(Long authorId) {
        return storage.values().stream()
                .filter(issue -> issue.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }
}