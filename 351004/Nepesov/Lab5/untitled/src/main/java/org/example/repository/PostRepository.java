package org.example.repository;

import org.example.model.Post;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends CassandraRepository<Post, Long> {
}