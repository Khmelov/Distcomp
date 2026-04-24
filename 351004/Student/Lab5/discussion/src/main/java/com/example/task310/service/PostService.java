package com.example.task310.service;

import com.example.task310.dto.*;
import com.example.task310.entity.Post;
import com.example.task310.enums.PostState;
import com.example.task310.mapper.EntityMapper;
import com.example.task310.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final EntityMapper mapper;

    public List<PostResponseTo> getAll(Pageable pageable) {
        return mapper.toPostResponseList(repository.findAll(pageable).getContent());
    }

    public PostResponseTo getById(Long id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public PostResponseTo create(PostRequestTo dto) {
        Post entity = mapper.toEntity(dto);
        if (entity.getId() == null) {
            entity.setId(Math.abs(new Random().nextLong()));
        }
        if (entity.getCountry() == null) {
            entity.setCountry("default");
        }
        return mapper.toResponse(repository.save(entity));
    }

    public PostResponseTo update(PostRequestTo dto) {
        if (!repository.existsById(dto.getId())) {
            throw new RuntimeException("Post not found");
        }
        Post entity = mapper.toEntity(dto);
        if (entity.getCountry() == null) entity.setCountry("default");
        if (entity.getState() == null) {
            entity.setState(PostState.APPROVE);
        }
        return mapper.toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Post not found");
        }
        repository.deleteById(id);
    }
}