package org.rv.lab1.discussion.repo;

import org.rv.lab1.discussion.domain.CommentById;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentByIdRepository extends CassandraRepository<CommentById, Long> {
}

