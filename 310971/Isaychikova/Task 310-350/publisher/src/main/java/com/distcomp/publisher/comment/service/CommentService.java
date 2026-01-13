package com.distcomp.publisher.comment.service;

import com.distcomp.publisher.article.domain.Article;
import com.distcomp.publisher.article.repo.ArticleRepository;
import com.distcomp.publisher.comment.domain.Comment;
import com.distcomp.publisher.comment.dto.CommentRequestTo;
import com.distcomp.publisher.comment.dto.CommentResponseTo;
import com.distcomp.publisher.comment.repo.CommentRepository;
import com.distcomp.publisher.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository repository;
    private final ArticleRepository storyRepository;

    public CommentService(CommentRepository repository, ArticleRepository storyRepository) {
        this.repository = repository;
        this.storyRepository = storyRepository;
    }

    public CommentResponseTo create(CommentRequestTo request) {
        Article story = storyRepository.findById(request.getStoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Story with id=" + request.getStoryId() + " not found"));

        Comment comment = new Comment();
        comment.setStory(story);
        comment.setContent(request.getContent());
        return toResponse(repository.save(comment));
    }

    public Optional<CommentResponseTo> get(long id) {
        return repository.findById(id).map(this::toResponse);
    }

    public List<CommentResponseTo> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public Optional<CommentResponseTo> update(long id, CommentRequestTo request) {
        return repository.findById(id).map(existing -> {
            Article story = storyRepository.findById(request.getStoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Story with id=" + request.getStoryId() + " not found"));
            existing.setStory(story);
            existing.setContent(request.getContent());
            return toResponse(repository.save(existing));
        });
    }

    public boolean delete(long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    private CommentResponseTo toResponse(Comment c) {
        long storyId = 0;
        if (c.getStory() != null && c.getStory().getId() != null) {
            storyId = c.getStory().getId();
        }
        return new CommentResponseTo(c.getId() != null ? c.getId() : 0, storyId, c.getContent());
    }
}
