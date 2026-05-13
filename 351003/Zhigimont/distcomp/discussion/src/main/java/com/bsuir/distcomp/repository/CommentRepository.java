package com.bsuir.distcomp.repository;

import com.bsuir.distcomp.entity.Comment;
import com.bsuir.distcomp.entity.CommentKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CassandraRepository<Comment, CommentKey> {
    List<Comment> findByKeyTopicId(Long topicId);

    @Query("SELECT * FROM tbl_comment WHERE id = ?0 ALLOW FILTERING")
    Optional<Comment> findById(Long id);
}