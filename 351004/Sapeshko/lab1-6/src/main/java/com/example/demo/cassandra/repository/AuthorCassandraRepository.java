package com.example.demo.cassandra.repository;

import com.example.demo.cassandra.model.AuthorCassandra;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

@Repository
@Profile("cassandra")
public interface AuthorCassandraRepository extends CassandraRepository<AuthorCassandra, Long> {
}