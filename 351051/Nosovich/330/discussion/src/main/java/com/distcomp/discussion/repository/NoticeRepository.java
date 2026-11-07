package com.distcomp.discussion.repository;

import com.distcomp.discussion.model.Notice;
import com.distcomp.discussion.model.NoticeKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

/**
 * Access to the issue-partitioned notice table.
 */
public interface NoticeRepository extends CassandraRepository<Notice, NoticeKey> {

    /**
     * @param issueId partition key of the issue
     * @return notices of the issue ordered by the clustering column
     */
    List<Notice> findByKeyIssueId(Long issueId);
}
