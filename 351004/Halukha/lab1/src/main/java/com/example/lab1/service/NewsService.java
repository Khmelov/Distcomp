package com.example.lab1.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.lab1.dto.NewsRequestTo;
import com.example.lab1.dto.NewsResponseTo;
import com.example.lab1.dto.UserResponseTo;
import com.example.lab1.exception.EntityNotFoundException;
import com.example.lab1.mapper.NewsMapper;
import com.example.lab1.mapper.UserMapper;
import com.example.lab1.model.News;
import com.example.lab1.model.User;
import com.example.lab1.repository.NewsRepository;
import com.example.lab1.repository.UserRepository;

@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final NewsMapper mapper = NewsMapper.INSTANCE;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public NewsService(NewsRepository newsRepository, UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
    }

    public List<NewsResponseTo> getAllNews() {
        return newsRepository.getAllEntities().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public NewsResponseTo getNewsById(Long id) {
        return newsRepository.getEntityById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("News not found", 40401));
    }

    public NewsResponseTo createNews(NewsRequestTo request) {
        if (!userRepository.existsEntity(request.getUserId())) {
            throw new EntityNotFoundException("User not found", 40401);
        }
        News news = mapper.toEntity(request);
        News saved = newsRepository.createEntity(news);
        return mapper.toDto(saved);
    }

    public NewsResponseTo updateNews(Long id, NewsRequestTo request) {
        News existing = newsRepository.getEntityById(id)
                .orElseThrow(() -> new EntityNotFoundException("News not found", 40401));
        News updated = mapper.updateEntity(request, existing);
        updated.setId(id);
        News saved = newsRepository.createEntity(updated);
        return mapper.toDto(saved);
    }

    public void deleteNews(Long id) {
        if (!newsRepository.existsEntity(id)) {
            throw new EntityNotFoundException("News not found", 40401);
        }
        newsRepository.deleteEntity(id);
    }

    public UserResponseTo getUserByNewsId(Long newsId) {
        News news = newsRepository.getEntityById(newsId)
            .orElseThrow(() -> new EntityNotFoundException("News not found", 40401));

        User user = userRepository.getEntityById(news.getUserId())
            .orElseThrow(() -> new EntityNotFoundException("User not found", 40401));

        return userMapper.toDto(user);
    }
}
