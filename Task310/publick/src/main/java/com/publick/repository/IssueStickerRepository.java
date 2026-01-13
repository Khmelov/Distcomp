package com.publick.repository;

import com.publick.entity.IssueSticker;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class IssueStickerRepository extends InMemoryCrudRepository<IssueSticker, Long> {

    private final AtomicLong compositeIdGenerator = new AtomicLong(1);

    @Override
    protected Long getId(IssueSticker entity) {
        // For composite key, we'll generate a unique ID
        return entity.getIssueId() != null && entity.getStickerId() != null ?
               (long) (entity.getIssueId() + "_" + entity.getStickerId()).hashCode() : null;
    }

    @Override
    protected void setId(IssueSticker entity, Long id) {
        // Composite key doesn't need setting
    }

    @Override
    protected Long generateId() {
        return compositeIdGenerator.getAndIncrement();
    }

    public List<IssueSticker> findByIssueId(Long issueId) {
        return storage.values().stream()
                .filter(relation -> relation.getIssueId().equals(issueId))
                .collect(Collectors.toList());
    }

    public List<IssueSticker> findByStickerId(Long stickerId) {
        return storage.values().stream()
                .filter(relation -> relation.getStickerId().equals(stickerId))
                .collect(Collectors.toList());
    }

    public void deleteByIssueId(Long issueId) {
        storage.entrySet().removeIf(entry -> entry.getValue().getIssueId().equals(issueId));
    }

    public void deleteByStickerId(Long stickerId) {
        storage.entrySet().removeIf(entry -> entry.getValue().getStickerId().equals(stickerId));
    }

    public boolean existsByIssueIdAndStickerId(Long issueId, Long stickerId) {
        return storage.values().stream()
                .anyMatch(relation -> relation.getIssueId().equals(issueId) && relation.getStickerId().equals(stickerId));
    }
}