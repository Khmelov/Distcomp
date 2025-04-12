package com.example.rest.service.Impl;

import com.example.rest.dto.post.PostRequestTo;
import com.example.rest.dto.post.PostResponseTo;
import com.example.rest.dto.post.PostUpdate;
import com.example.rest.entity.Creator;
import com.example.rest.entity.Post;
import com.example.rest.entity.Topic;
import com.example.rest.exceptionHandler.CreatorNotFoundException;
import com.example.rest.mapper.PostMapper;
import com.example.rest.repository.PostRepository;
import com.example.rest.repository.TopicRepository;
import com.example.rest.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private TopicRepository topicRepository;
    private PostMapper postMapper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper, TopicRepository topicRepository) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.topicRepository = topicRepository;
    }
    
    @Override
    public PostResponseTo create(PostRequestTo post) {
        topicRepository.findById(post.getTopicId())
                .orElseThrow(() -> new CreatorNotFoundException(post.getTopicId()));
        return postMapper.toResponse(postRepository.save(postMapper.toEntity(post)));
    }

    @Override
    public PostResponseTo update(PostUpdate updatedPost) {
        Post post = postRepository.findById(updatedPost.getId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (updatedPost.getId() != null) {
            post.setId(updatedPost.getId());
        }
        if (updatedPost.getContent() != null) {
            post.setContent(updatedPost.getContent());
        }
        if (updatedPost.getTopicId() != null) {
            Topic topic = topicRepository.findById(updatedPost.getTopicId())
                    .orElseThrow(() -> new IllegalArgumentException("Topic to update not found"));
            post.setTopic(topic);
        }

        return postMapper.toResponse(postRepository.save(post));
    }

    @Override
    public void deleteById(Long id) {
        postRepository.deleteById(id);

    }

    @Override
    public List<PostResponseTo> findAll() {
        return StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .map(postMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<PostResponseTo> findById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toResponse);
    }
}
