package com.sergey.orsik.discussion.repository;

import com.sergey.orsik.discussion.cassandra.CommentByIdRow;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface CommentByIdRepository extends CassandraRepository<CommentByIdRow, Long> {
}
