package org.example.repository;

import org.example.model.Comment;
import org.example.model.CommentKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CassandraRepository<Comment, CommentKey> {


    List<Comment> findByKeyCountryAndKeyTweetId(String country, Long tweetId);

    @Query("SELECT * FROM tbl_comment WHERE id = ?0 ALLOW FILTERING")
    List<Comment> findByIdAllowFiltering(Long id);
}