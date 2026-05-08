package com.example.forum.service.impl;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.entity.Post;
import com.example.forum.entity.Topic;
import com.example.forum.exception.BadRequestException;
import com.example.forum.exception.NotFoundException;
import com.example.forum.mapper.PostMapper;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.TopicRepository;
import com.example.forum.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import org.springframework.data.domain.Pageable;
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository repository;
    private final PostMapper mapper;
    private final TopicRepository topicRepository;

    public PostServiceImpl(PostRepository repository, PostMapper mapper, TopicRepository topicRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.topicRepository = topicRepository;
    }

    @Override
    public PostResponseTo create(PostRequestTo request) {
        validate(request);

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new NotFoundException("Topic not found", "40434"));

        Post post = mapper.toEntity(request);
        post.setTopic(topic);

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
    public Page<PostResponseTo> getAll(Long topicId, Pageable pageable) {

        Page<Post> page = (topicId == null)
                ? repository.findAll(pageable)
                : repository.findByTopicId(topicId, pageable);

        return page.map(mapper::toResponse);
    }


    @Override
    public PostResponseTo update(Long id, PostRequestTo request) {
        validate(request);

        Post existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found", "40432"));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new NotFoundException("Topic not found", "40434"));

        existing.setTopic(topic);
        existing.setContent(request.getContent());

        Post updated = repository.save(existing);
        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
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
