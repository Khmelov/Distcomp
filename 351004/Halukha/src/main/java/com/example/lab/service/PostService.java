package com.example.lab.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.lab.dto.PostRequestTo;
import com.example.lab.dto.PostResponseTo;
import com.example.lab.exception.EntityNotFoundException;
import com.example.lab.mapper.PostMapper;
import com.example.lab.model.News;
import com.example.lab.model.Post;
import com.example.lab.repository.NewsRepository;
import com.example.lab.repository.PostRepository;

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
        return postRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public PostResponseTo getPostById(Long id) {
        return postRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Post not found", 40401));
    }

    public PostResponseTo createPost(PostRequestTo request) {
        if (!newsRepository.existsById(request.getNewsId())) {
            throw new EntityNotFoundException("News not found", 40401);
        }
        Post news = mapper.toEntity(request);
        Post saved = postRepository.save(news);
        return mapper.toDto(saved);
    }

    public PostResponseTo updatePost(Long id, PostRequestTo request) {
        Post existing = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found", 40401));
        Post updated = mapper.updateEntity(request, existing);
        updated.setId(id);
        Post saved = postRepository.save(updated);
        return mapper.toDto(saved);
    }

    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new EntityNotFoundException("Post not found", 40401);
        }
        postRepository.deleteById(id);
    }

    public List<PostResponseTo> getAllPostByNewsId(Long userId) {
        List<News> news = newsRepository.findAll().stream()
                .filter(news1 -> news1.getUserId().equals(userId))
                .collect(Collectors.toList());
        return postRepository.findAll().stream()
                .filter(post -> news.stream().anyMatch(post1 -> post1.getId().equals(post.getNewsId())))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
