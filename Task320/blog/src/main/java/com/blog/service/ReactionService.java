package com.blog.service;

import com.blog.dto.ReactionRequestTo;
import com.blog.dto.ReactionResponseTo;
import com.blog.entity.Article;
import com.blog.entity.Reaction;
import com.blog.exception.EntityNotFoundException;
import com.blog.mapper.ReactionMapper;
import com.blog.repository.ArticleRepository;
import com.blog.repository.ReactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final ArticleRepository articleRepository;
    private final ReactionMapper reactionMapper;

    public ReactionService(ReactionRepository reactionRepository, ArticleRepository articleRepository, ReactionMapper reactionMapper) {
        this.reactionRepository = reactionRepository;
        this.articleRepository = articleRepository;
        this.reactionMapper = reactionMapper;
    }

    public List<ReactionResponseTo> findAll() {
        return reactionRepository.findAll().stream()
                .map(reactionMapper::entityToResponseTo)
                .collect(Collectors.toList());
    }

    public ReactionResponseTo findById(Long id) {
        Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found with id: " + id));
        return reactionMapper.entityToResponseTo(reaction);
    }

    public ReactionResponseTo create(ReactionRequestTo request) {
        // Get article entity
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + request.getArticleId()));

        Reaction reaction = reactionMapper.requestToToEntity(request);
        reaction.setArticle(article);
        reaction.setCreated(java.time.LocalDateTime.now());
        reaction.setModified(java.time.LocalDateTime.now());
        Reaction savedReaction = reactionRepository.save(reaction);
        return reactionMapper.entityToResponseTo(savedReaction);
    }

    public ReactionResponseTo update(Long id, ReactionRequestTo request) {
        Reaction existingReaction = reactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found with id: " + id));

        // Get article entity
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + request.getArticleId()));

        reactionMapper.updateEntityFromRequest(request, existingReaction);
        existingReaction.setArticle(article);
        existingReaction.setModified(java.time.LocalDateTime.now());
        Reaction updatedReaction = reactionRepository.save(existingReaction);
        return reactionMapper.entityToResponseTo(updatedReaction);
    }

    public void deleteById(Long id) {
        if (!reactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Reaction not found with id: " + id);
        }
        reactionRepository.deleteById(id);
    }

    public List<ReactionResponseTo> findByArticleId(Long articleId) {
        return reactionRepository.findByArticle_Id(articleId).stream()
                .map(reactionMapper::entityToResponseTo)
                .collect(Collectors.toList());
    }
}