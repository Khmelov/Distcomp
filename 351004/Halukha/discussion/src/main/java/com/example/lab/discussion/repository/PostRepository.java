package com.example.lab.discussion.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import com.example.lab.discussion.model.Post;

public interface PostRepository extends CassandraRepository<Post, Long> {
}
