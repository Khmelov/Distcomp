package org.polozkov.repository.issue;

import org.polozkov.entity.issue.Issue;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class IssueRepository {

    private final ConcurrentHashMap<Long, Issue> issues = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Issue> findAll() {
        return new ArrayList<>(issues.values());
    }

    public Optional<Issue> findById(Long id) {
        return Optional.ofNullable(issues.get(id));
    }

    public Issue save(Issue issue) {
        if (issue.getId() == null) {
            issue.setId(idCounter.getAndIncrement());
        }
        issues.put(issue.getId(), issue);
        return issue;
    }

    public Issue update(Issue issue) {
        if (!issues.containsKey(issue.getId())) {
            throw new RuntimeException("Issue not found with id: " + issue.getId());
        }
        issues.put(issue.getId(), issue);
        return issue;
    }

    public void deleteById(Long id) {
        issues.remove(id);
    }

    public boolean existsById(Long id) {
        return issues.containsKey(id);
    }

    public List<Issue> findByUserId(Long userId) {
        return issues.values().stream()
                .filter(issue -> issue.getUser().getId().equals(userId))
                .toList();
    }
}
