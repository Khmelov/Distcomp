package com.example.task310.service;

import com.example.task310.dto.PostRequestTo;
import com.example.task310.dto.PostResponseTo;
import com.example.task310.mapper.EntityMapper;
import com.example.task310.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final EntityMapper mapper;

    public PostResponseTo create(PostRequestTo dto) {
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    public PostResponseTo getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public List<PostResponseTo> getAll() {
        return mapper.toPostResponseList(repository.findAll());
    }

    public PostResponseTo update(PostRequestTo dto) {
        if (repository.findById(dto.getId()).isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        repository.deleteById(id);
    }
}