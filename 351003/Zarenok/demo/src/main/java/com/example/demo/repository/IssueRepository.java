package com.example.demo.repository;

import com.example.demo.model.Issue;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class IssueRepository extends InMemoryRepository<Issue, Long>{
    @Override
    protected Long generatedId() {
        return id++;
    }

    public List<Issue> findByAuthorId(Long authorId) {
        return storage.values().stream()
                .filter(issue -> issue.getAuthorId() != null && issue.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }

    public List<Issue> findByMarksId(Long markId) {
        return storage.values().stream()
                .filter(issue -> issue.getMarks() != null &&
                        issue.getMarks().stream().anyMatch(mark -> mark.getId().equals(markId)))
                .collect(Collectors.toList());
    }

    @Override
    protected Long getId(Issue entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Issue entity, Long id) {
        entity.setId(id);
    }
}
