package com.lizaveta.discussion.repository;

import com.lizaveta.discussion.cassandra.NoticeByIdRow;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface NoticeByIdCassandraRepository extends CassandraRepository<NoticeByIdRow, Long> {
}
