package com.aitor.discussion.repository;

import com.aitor.discussion.model.Message;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface MessageRepository extends CassandraRepository<Message, Long> {
}
