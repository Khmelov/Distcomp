package com.adashkevich.rest.lab.service;

import com.adashkevich.rest.lab.dto.request.MessageRequestTo;
import com.adashkevich.rest.lab.dto.response.MessageResponseTo;
import com.adashkevich.rest.lab.exception.NotFoundException;
import com.adashkevich.rest.lab.exception.ValidationException;
import com.adashkevich.rest.lab.mapper.MessageMapper;
import com.adashkevich.rest.lab.model.Message;
import com.adashkevich.rest.lab.repository.MessageRepository;
import com.adashkevich.rest.lab.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private final MessageRepository repo;
    private final NewsRepository newsRepo;
    private final MessageMapper mapper;

    public MessageService(MessageRepository repo, NewsRepository newsRepo, MessageMapper mapper) {
        this.repo = repo;
        this.newsRepo = newsRepo;
        this.mapper = mapper;
    }

    public MessageResponseTo create(MessageRequestTo dto) {
        ensureNewsExists(dto.newsId);
        Message entity = mapper.toEntity(dto);
        Message saved = repo.save(entity);
        return mapper.toResponse(saved);
    }

    public List<MessageResponseTo> getAll() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    public MessageResponseTo getById(Long id) {
        Message m = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Message not found", "40440"));
        return mapper.toResponse(m);
    }

    public MessageResponseTo update(Long id, MessageRequestTo dto) {
        Message existing = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Message not found", "40440"));

        ensureNewsExists(dto.newsId);
        existing.setNewsId(dto.newsId);
        existing.setContent(dto.content);

        Message updated = repo.update(id, existing);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Message not found", "40440");
        }
        repo.deleteById(id);
    }

    private void ensureNewsExists(Long newsId) {
        if (newsId == null || !newsRepo.existsById(newsId)) {
            throw new ValidationException("newsId does not exist", "40020");
        }
    }
}
