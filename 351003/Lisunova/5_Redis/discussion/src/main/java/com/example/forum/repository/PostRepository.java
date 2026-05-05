package com.example.forum.repository;

import com.example.forum.entity.Post;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.util.List;
import com.example.forum.entity.Post;
import org.springframework.data.cassandra.repository.CassandraRepository;
import java.util.Optional;

public interface PostRepository extends CassandraRepository<Post, Long> {
    Slice<Post> findAllByTopicId(Long topicId, Pageable pageable);
    Optional<Post> findByTopicIdAndId(Long topicId, Long id);
    Optional<Post> findById(Long id);
}