package com.example.discussion.repository;

import com.example.discussion.model.PostById;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface PostByIdRepository extends CassandraRepository<PostById, Long> {
}
