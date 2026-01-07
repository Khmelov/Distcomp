package com.task310.discussion.repository;

import com.task310.discussion.model.Post;
import com.task310.discussion.model.PostKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CassandraRepository<Post, PostKey> {
    @Query("SELECT * FROM tbl_post WHERE article_id = ?0")
    List<Post> findByArticleId(Long articleId);
    
    @Query("SELECT * FROM tbl_post WHERE article_id = ?0 AND id = ?1")
    Optional<Post> findByArticleIdAndId(Long articleId, Long id);
    
    List<Post> findAll();
}

