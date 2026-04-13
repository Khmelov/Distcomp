package com.example.task310.service;

import com.example.task310.dto.*;
import com.example.task310.mapper.EntityMapper;
import com.example.task310.repository.IssueRepository;
import com.example.task310.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final EntityMapper mapper;
    private final IssueRepository issueRepository;

    public List<PostResponseTo> getAll(Pageable pageable) {
        return mapper.toPostResponseList(repository.findAll(pageable).getContent());
    }

    public PostResponseTo getById(Long id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public PostResponseTo create(PostRequestTo dto) {
        if (!issueRepository.existsById(dto.getIssueId())) {
            throw new RuntimeException("Issue not found");
        }
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    public PostResponseTo update(PostRequestTo dto) {
        if (!repository.existsById(dto.getId())) {
            throw new RuntimeException("Post not found");
        }
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Post not found");
        }
        repository.deleteById(id);
    }
}