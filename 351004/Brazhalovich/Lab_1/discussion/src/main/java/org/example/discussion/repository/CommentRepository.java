package org.example.discussion.repository;

import org.example.discussion.entity.Comment;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CassandraRepository<Comment, Long> {

    @Query("SELECT * FROM tbl_comment WHERE news_id = ?0 ALLOW FILTERING")
    List<Comment> findByNewsId(Long newsId);

    Optional<Comment> findById(Long id);
}