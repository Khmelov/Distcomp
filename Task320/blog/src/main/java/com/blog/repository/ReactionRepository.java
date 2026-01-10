package com.blog.repository;

import com.blog.entity.Reaction;

import java.util.List;

public interface ReactionRepository extends BaseJpaRepository<Reaction, Long> {
    List<Reaction> findByArticle_Id(Long articleId);
}