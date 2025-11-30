package org.discussion.repository;

import org.discussion.model.Notice;
import org.discussion.model.NoticeKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends CassandraRepository<Notice, NoticeKey> {
}