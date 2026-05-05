package com.example.forum.service.impl;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.entity.Post;
import com.example.forum.exception.NotFoundException;
import com.example.forum.repository.PostRepository;
import com.example.forum.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository repository;

    @Override
    public PostResponseTo create(PostRequestTo request) {
        Post post = new Post();
        post.setId(System.currentTimeMillis());
        post.setTopicId(request.getTopicId());
        post.setContent(request.getContent());

        Post saved = repository.save(post);
        return mapToResponse(saved);
    }

    @Override
    public PostResponseTo getById(Long id) {
        return repository.findAll().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Post not found", "40431"));
    }

    @Override
    public List<PostResponseTo> getAll(Long topicId, Pageable pageable) {
        if (topicId == null) {
            return List.of();
        }

        var slice = repository.findAllByTopicId(topicId, pageable);

        if (slice == null) return List.of();

        return slice.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponseTo update(Long id, PostRequestTo request) {
        Post existing = repository.findAll().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Post not found", "40432"));

        existing.setContent(request.getContent());
        Post updated = repository.save(existing);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Post existing = repository.findAll().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Post not found", "40423"));

        repository.delete(existing);
    }

    private PostResponseTo mapToResponse(Post post) {
        PostResponseTo response = new PostResponseTo();
        response.setId(post.getId());
        response.setTopicId(post.getTopicId());
        response.setContent(post.getContent());
        return response;
    }
}