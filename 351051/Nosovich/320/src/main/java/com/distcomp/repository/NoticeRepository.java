package com.distcomp.repository;

import com.distcomp.model.Notice;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Storage of {@link Notice} entities.
 */
@Repository
public interface NoticeRepository extends BaseRepository<Notice> {

    /**
     * @param issueId identifier of the related issue
     * @return every notice attached to this issue
     */
    List<Notice> findByIssueId(Long issueId);

    /**
     * @param issueId identifier of the related issue
     * @return number of notices attached to this issue
     */
    long countByIssueId(Long issueId);

    /**
     * Removes every notice attached to the given issue.
     *
     * @param issueId identifier of the related issue
     */
    void deleteByIssueId(Long issueId);
}
