package com.distcomp.discussion.repository;

import com.distcomp.discussion.model.NoticeById;
import org.springframework.data.cassandra.repository.CassandraRepository;

/**
 * Lookup table keyed by the notice identifier.
 */
public interface NoticeByIdRepository extends CassandraRepository<NoticeById, Long> {
}
