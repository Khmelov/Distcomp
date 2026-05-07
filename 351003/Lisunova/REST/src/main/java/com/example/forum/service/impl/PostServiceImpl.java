package com.example.forum.service.impl;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.entity.Post;
import com.example.forum.exception.BadRequestException;
import com.example.forum.exception.NotFoundException;
import com.example.forum.mapper.PostMapper;
import com.example.forum.repository.PostRepository;
import com.example.forum.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository repository;
    private final PostMapper mapper;

    public PostServiceImpl(PostRepository repository, PostMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PostResponseTo create(PostRequestTo request) {
        validate(request);
        Post post = mapper.toEntity(request);
        Post saved = repository.save(post);
        return mapper.toResponse(saved);
    }

    @Override
    public PostResponseTo getById(Long id) {
        Post post = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found", "40431"));
        return mapper.toResponse(post);
    }

    @Override
    public List<PostResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public PostResponseTo update(Long id, PostRequestTo request) {
        validate(request);

        Post existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found", "40432"));

        existing.setTopicId(request.getTopicId());
        existing.setContent(request.getContent());

        Post updated = repository.update(existing);
        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("Post not found", "40423");
        }
        repository.deleteById(id);
    }


    private void validate(PostRequestTo request) {
        if (request.getTopicId() == null) {
            throw new BadRequestException("TopicId is required", "40031");
        }
        if (!StringUtils.hasText(request.getContent()) ||
                request.getContent().length() < 2 ||
                request.getContent().length() > 2048) {
            throw new BadRequestException("Invalid content", "40032");
        }
    }
}
