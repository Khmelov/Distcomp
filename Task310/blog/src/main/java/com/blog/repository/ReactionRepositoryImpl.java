package com.blog.repository;

import com.blog.entity.Reaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ReactionRepositoryImpl extends InMemoryGenericRepository<Reaction> implements ReactionRepository {

    @Override
    public List<Reaction> findByArticleId(Long articleId) {
        return storage.values().stream()
                .filter(reaction -> articleId.equals(reaction.getArticleId()))
                .collect(Collectors.toList());
    }
}