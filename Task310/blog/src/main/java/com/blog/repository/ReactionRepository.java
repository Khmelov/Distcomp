package com.blog.repository;

import com.blog.entity.Reaction;

import java.util.List;

public interface ReactionRepository extends GenericRepository<Reaction, Long> {
    List<Reaction> findByArticleId(Long articleId);
}