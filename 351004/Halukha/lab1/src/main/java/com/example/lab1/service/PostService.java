package com.example.lab1.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.lab1.dto.PostRequestTo;
import com.example.lab1.dto.PostResponseTo;
import com.example.lab1.exception.EntityNotFoundException;
import com.example.lab1.mapper.PostMapper;
import com.example.lab1.model.News;
import com.example.lab1.model.Post;
import com.example.lab1.repository.NewsRepository;
import com.example.lab1.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final NewsRepository newsRepository;
    private final PostMapper mapper = PostMapper.INSTANCE;

    public PostService(PostRepository postRepository, NewsRepository newsRepository) {
        this.postRepository = postRepository;
        this.newsRepository = newsRepository;
    }

    public List<PostResponseTo> getAllPost() {
        return postRepository.getAllEntities().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public PostResponseTo getPostById(Long id) {
        return postRepository.getEntityById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Post not found", 40401));
    }

    public PostResponseTo createPost(PostRequestTo request) {
        Post news = mapper.toEntity(request);
        Post saved = postRepository.createEntity(news);
        return mapper.toDto(saved);
    }

    public PostResponseTo updatePost(Long id, PostRequestTo request) {
        Post existing = postRepository.getEntityById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found", 40401));
        Post updated = mapper.updateEntity(request, existing);
        updated.setId(id);
        Post saved = postRepository.createEntity(updated);
        return mapper.toDto(saved);
    }

    public void deletePost(Long id) {
        if (!postRepository.existsEntity(id)) {
            throw new EntityNotFoundException("Post not found", 40401);
        }
        postRepository.deleteEntity(id);
    }

    public List<PostResponseTo> getAllPostByNewsId(Long userId) {
        List<News> news = newsRepository.getAllEntities().stream()
                .filter(news1 -> news1.getUserId().equals(userId))
                .collect(Collectors.toList());
        return postRepository.getAllEntities().stream()
                .filter(post -> news.stream().anyMatch(post1 -> post1.getId().equals(post.getNewsId())))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
