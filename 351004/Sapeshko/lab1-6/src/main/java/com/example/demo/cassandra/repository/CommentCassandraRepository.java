package com.example.demo.cassandra.repository;

import com.example.demo.cassandra.model.CommentCassandra;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.context.annotation.Profile;

@Profile("cassandra")
public interface CommentCassandraRepository extends CassandraRepository<CommentCassandra, Long> {
}