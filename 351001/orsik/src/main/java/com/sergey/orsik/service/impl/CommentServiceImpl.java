package com.sergey.orsik.service.impl;

import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import com.sergey.orsik.entity.Comment;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.mapper.CommentMapper;
import com.sergey.orsik.repository.CrudRepository;
import com.sergey.orsik.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CrudRepository<Comment> repository;
    private final CommentMapper mapper;

    public CommentServiceImpl(CrudRepository<Comment> repository, CommentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<CommentResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CommentResponseTo findById(Long id) {
        Comment entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment", id));
        return mapper.toResponse(entity);
    }

    @Override
    public CommentResponseTo create(CommentRequestTo request) {
        Comment entity = mapper.toEntity(request);
        entity.setId(null);
        Comment saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public CommentResponseTo update(Long id, CommentRequestTo request) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Comment", id);
        }
        Comment entity = mapper.toEntity(request);
        entity.setId(id);
        Comment saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException("Comment", id);
        }
    }
}
