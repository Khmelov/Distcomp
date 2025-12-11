package org.example.service;

import org.example.dto.CommentRequestTo;
import org.example.dto.CommentResponseTo;
import org.example.mapper.CommentMapper;
import org.example.model.Comment;
import org.example.model.CommentKey;
import org.example.repository.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository repository;
    private final CommentMapper mapper;

    public CommentService(CommentRepository repository, CommentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public CommentResponseTo create(CommentRequestTo dto) {
        Comment entity = mapper.toEntity(dto);

        CommentKey key = new CommentKey("ru", dto.getTweetId(), generateId());
        entity.setKey(key);

        Comment saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public List<CommentResponseTo> getByTweet(String country, Long tweetId) {
        return repository.findByKeyCountryAndKeyTweetId(country, tweetId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponseTo> getAllComments() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private Long generateId() {
        return System.currentTimeMillis();
    }


    public CommentResponseTo getById(Long id) {
        List<Comment> list = repository.findByIdAllowFiltering(id);
        if (list == null || list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Comment not found with id: " + id);
        }
        return mapper.toResponse(list.get(0));
    }

    public CommentResponseTo update(Long id, CommentRequestTo dto) {
        List<Comment> list = repository.findByIdAllowFiltering(id);
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found with id: " + id);
        }

        Comment existing = list.get(0);
        existing.setContent(dto.getContent());
        Comment saved = repository.save(existing);
        return mapper.toResponse(saved);
    }

    public void delete(Long id) {
        List<Comment> list = repository.findByIdAllowFiltering(id);
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found with id: " + id);
        }
        repository.delete(list.get(0));
    }
}