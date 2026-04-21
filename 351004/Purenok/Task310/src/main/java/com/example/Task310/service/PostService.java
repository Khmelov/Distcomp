package com.example.Task310.service;

import com.example.Task310.bean.Post;
import com.example.Task310.dto.PostRequestTo;
import com.example.Task310.dto.PostResponseTo;
import com.example.Task310.exception.ResourceNotFoundException;
import com.example.Task310.mapper.PostMapper;
import com.example.Task310.repository.InMemoryPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final InMemoryPostRepository repository;
    private final PostMapper mapper;

    public PostResponseTo create(PostRequestTo request) {
        Post post = mapper.toEntity(request);
        return mapper.toDto(repository.save(post));
    }

    public List<PostResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public PostResponseTo getById(Long id) {
        Post post = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return mapper.toDto(post);
    }

    public PostResponseTo update(Long id, PostRequestTo request) {
        Post post = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        mapper.updateEntityFromDto(request, post);
        return mapper.toDto(repository.update(post));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        repository.deleteById(id);
    }
}