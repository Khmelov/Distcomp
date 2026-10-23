package com.distcomp.repository;

import com.distcomp.model.Notice;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * In-memory storage of {@link Notice} entities.
 */
@Repository
public class NoticeRepository extends InMemoryRepository<Notice> {

    /**
     * @param issueId identifier of the related issue
     * @return every notice attached to this issue
     */
    public List<Notice> findByIssueId(Long issueId) {
        return findAll().stream()
                .filter(notice -> issueId != null && issueId.equals(notice.getIssueId()))
                .toList();
    }
}
