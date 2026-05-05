package com.example.forum.repository;

import com.example.forum.entity.Post;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.util.List;

public interface PostRepository extends CassandraRepository<Post, Long> {
    Slice<Post> findAllByTopicId(Long topicId, Pageable pageable);
    List<Post> findAllByTopicId(Long topicId);
}