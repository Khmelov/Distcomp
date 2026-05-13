package com.example.Task310.service;

import com.example.Task310.bean.Post;
import com.example.Task310.bean.Story;
import com.example.Task310.dto.PostRequestTo;
import com.example.Task310.dto.PostResponseTo;
import com.example.Task310.exception.ResourceNotFoundException;
import com.example.Task310.mapper.PostMapper;
import com.example.Task310.repository.PostRepository;
import com.example.Task310.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final StoryRepository storyRepository;
    private final PostMapper mapper;

    @Transactional
    public PostResponseTo create(PostRequestTo request) {
        Story story = storyRepository.findById(request.getStoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Story association not found"));

        Post post = mapper.toEntity(request);
        post.setStory(story);
        return mapper.toResponse(repository.save(post));
    }

    @Transactional
    public PostResponseTo update(Long id, PostRequestTo request) {
        Post existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Story story = storyRepository.findById(request.getStoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Story association not found"));

        mapper.updateEntity(request, existing);
        existing.setStory(story);
        return mapper.toResponse(repository.save(existing));
    }

    public List<PostResponseTo> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PostResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found");
        }
        repository.deleteById(id);
    }
}

