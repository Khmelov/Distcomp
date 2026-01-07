package com.task310.blogplatform.repository;

import com.task310.blogplatform.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);
    
    List<Post> findByArticleId(Long articleId);
    
    Page<Post> findByArticleId(Long articleId, Pageable pageable);
}

