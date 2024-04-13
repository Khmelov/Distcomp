package com.example.rv.impl.issue;

import com.example.rv.api.MemRepository.MemoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IssueRepository extends MemoryRepository<Issue> {
    @Override
    public Optional<Issue> save(Issue entity) {
        entity.id = ids.incrementAndGet();
        map.put(entity.getId(), entity);
        return Optional.of(entity);
    }

    @Override
    public Optional<Issue> update(Issue issue) {
        Long id = issue.getId();
        Issue memRepIssue = map.get(id);

        //maybe some checks for un existing users, but how??
        if (memRepIssue != null && (
                issue.getCreatorId() != null &&
                        issue.getTitle().length() > 1 &&
                        issue.getTitle().length() < 65 &&
                        issue.getContent().length() > 3 &&
                        issue.getContent().length() < 2049
                ) ){

        memRepIssue = issue;
        } else return Optional.empty();

        return Optional.of(memRepIssue);
    }

    @Override
    public boolean delete(Issue issue){
        return map.remove(issue.getId(), issue);
    }
}
