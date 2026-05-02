package com.example.demo.service;

import com.example.demo.dto.CommentRequest;
import com.example.demo.entity.Comment;
import com.example.demo.entity.News;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.NewsRepository;
import com.example.demo.sync.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Profile;
@Profile("docker")
@Service
public class CommentService extends AbstractGenericService<Comment, Long> {
    private final NewsRepository newsRepository;
    @Autowired
    private SyncService syncService;

    public CommentService(CommentRepository repository, NewsRepository newsRepository) {
        super(repository, repository);
        this.newsRepository = newsRepository;
    }

    @Transactional
    public Comment createFromRequest(CommentRequest request) {
        News news = newsRepository.findById(request.getNewsId())
                .orElseThrow(() -> new NotFoundException("News not found with id: " + request.getNewsId()));
        Comment comment = new Comment();
        comment.setNews(news);
        comment.setContent(request.getContent());
        Comment saved = repository.save(comment);
        syncService.syncComment(saved, "create");
        return saved;
    }

    @Transactional
    public Comment updateFromRequest(Long id, CommentRequest request) {
        Comment comment = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + id));
        News news = newsRepository.findById(request.getNewsId())
                .orElseThrow(() -> new NotFoundException("News not found with id: " + request.getNewsId()));
        comment.setNews(news);
        comment.setContent(request.getContent());
        Comment updated = repository.save(comment);
        syncService.syncComment(updated, "update");
        return updated;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Comment not found with id: " + id);
        }
        Comment existing = repository.findById(id).get();
        repository.deleteById(id);
        syncService.syncComment(existing, "delete");
    }
}