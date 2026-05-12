package com.adashkevich.jpa.lab.service;

import com.adashkevich.jpa.lab.dto.request.MessageRequestTo;
import com.adashkevich.jpa.lab.dto.response.MessageResponseTo;
import com.adashkevich.jpa.lab.exception.NotFoundException;
import com.adashkevich.jpa.lab.exception.ValidationException;
import com.adashkevich.jpa.lab.model.Message;
import com.adashkevich.jpa.lab.repository.MessageRepository;
import com.adashkevich.jpa.lab.repository.NewsRepository;
import com.adashkevich.jpa.lab.mapper.MessageMapper;
import com.adashkevich.jpa.lab.model.News;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MessageService {
    private final MessageRepository repo;
    private final NewsRepository newsRepo;
    private final MessageMapper mapper;

    public MessageService(MessageRepository repo, NewsRepository newsRepo, MessageMapper mapper) {
        this.repo = repo;
        this.newsRepo = newsRepo;
        this.mapper = mapper;
    }

    @Transactional
    public MessageResponseTo create(MessageRequestTo dto) {
        Message entity = mapper.toEntity(dto);
        entity.setNews(findNews(dto.newsId));
        return mapper.toResponse(repo.save(entity));
    }

    public List<MessageResponseTo> getAll() {
        return repo.findAll(Sort.by("id")).stream().map(mapper::toResponse).toList();
    }

    public MessageResponseTo getById(Long id) {
        return mapper.toResponse(findExisting(id));
    }

    @Transactional
    public MessageResponseTo update(Long id, MessageRequestTo dto) {
        Message existing = findExisting(id);
        existing.setNews(findNews(dto.newsId));
        existing.setContent(dto.content);
        return mapper.toResponse(repo.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        repo.delete(findExisting(id));
    }

    private Message findExisting(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Message not found", "40440"));
    }

    private News findNews(Long newsId) {
        return newsRepo.findById(newsId).orElseThrow(() -> new ValidationException("newsId does not exist", "40020"));
    }
}
