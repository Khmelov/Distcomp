package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.PostRequestTo;
import org.example.dto.PostResponseTo;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.PostMapper;
import org.example.model.Post;
import org.example.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final PostMapper mapper;

    public PostResponseTo create(PostRequestTo request) {
        Post entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public List<PostResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public PostResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
    }

    public PostResponseTo update(PostRequestTo request) {
        if (!repository.existsById(request.getId())) {
            throw new EntityNotFoundException("Cannot update: Post not found");
        }
        Post entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete: Post not found");
        }
        repository.deleteById(id);
    }
}