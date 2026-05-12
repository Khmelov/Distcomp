package com.example.demo.cassandra.repository;

import com.example.demo.cassandra.model.TagCassandra;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.context.annotation.Profile;

@Profile("cassandra")
public interface TagCassandraRepository extends CassandraRepository<TagCassandra, Long> {
}