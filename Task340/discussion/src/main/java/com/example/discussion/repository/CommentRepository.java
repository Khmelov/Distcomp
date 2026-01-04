package com.example.discussion.repository;

import com.example.discussion.model.Comment;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CassandraRepository<Comment, Long> {
    List<Comment> findByStoryId(Long storyId);
}