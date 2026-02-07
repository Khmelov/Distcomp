package com.example.demo.repository;

import com.example.demo.model.Issue;
import org.springframework.stereotype.Repository;

@Repository
public class IssueRepository extends InMemoryRepository<Issue, Long>{
    @Override
    protected Long generatedId() {
        return id++;
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
