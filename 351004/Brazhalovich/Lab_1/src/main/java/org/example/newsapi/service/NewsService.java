package org.example.newsapi.service;


import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.NewsRequestTo;
import org.example.newsapi.dto.response.NewsResponseTo;
import org.example.newsapi.entity.News;
import org.example.newsapi.exception.NotFoundException;
import org.example.newsapi.mapper.NewsMapper;
import org.example.newsapi.repository.impl.NewsRepository;
import org.example.newsapi.repository.impl.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final NewsMapper newsMapper;

    public NewsResponseTo create(NewsRequestTo request) {
        // Валидация существования юзера
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        News news = newsMapper.toEntity(request);
        news.setCreated(LocalDateTime.now());
        news.setModified(LocalDateTime.now());
        return newsMapper.toDto(newsRepository.save(news));
    }

    public List<NewsResponseTo> findAll() {
        return newsRepository.findAll().stream()
                .map(newsMapper::toDto)
                .collect(Collectors.toList());
    }

    public NewsResponseTo findById(Long id) {
        return newsRepository.findById(id)
                .map(newsMapper::toDto)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + id));
    }

    public NewsResponseTo update(Long id, NewsRequestTo request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + id));
        newsMapper.updateEntityFromDto(request, news);
        return newsMapper.toDto(newsRepository.update(news));
    }

    public void delete(Long id) {
        if (newsRepository.findById(id).isEmpty()) {
            throw new NotFoundException("News not found with id: " + id);
        }
        newsRepository.deleteById(id);
    }
}