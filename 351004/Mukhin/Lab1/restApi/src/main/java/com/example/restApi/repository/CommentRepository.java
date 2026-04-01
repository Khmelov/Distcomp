package com.example.restApi.repository;

import com.example.restApi.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface CommentRepository  extends JpaRepository<Comment, Long> {
    Page<Comment> findByArticle_Id(Long articleId, Pageable pageable);
}
