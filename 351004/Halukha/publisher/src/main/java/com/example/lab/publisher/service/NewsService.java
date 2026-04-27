package com.example.lab.publisher.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.example.lab.publisher.dto.NewsRequestTo;
import com.example.lab.publisher.dto.NewsResponseTo;
import com.example.lab.publisher.dto.UserResponseTo;
import com.example.lab.publisher.exception.EntityNotFoundException;
import com.example.lab.publisher.mapper.NewsMapper;
import com.example.lab.publisher.mapper.UserMapper;
import com.example.lab.publisher.model.Marker;
import com.example.lab.publisher.model.News;
import com.example.lab.publisher.model.User;
import com.example.lab.publisher.repository.MarkerRepository;
import com.example.lab.publisher.repository.NewsRepository;
import com.example.lab.publisher.repository.UserRepository;

@Service
public class NewsService {

    private final MarkerRepository markerRepository;
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final NewsMapper mapper = NewsMapper.INSTANCE;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public NewsService(MarkerRepository markerRepository, NewsRepository newsRepository,
            UserRepository userRepository) {
        this.markerRepository = markerRepository;
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
    }

    @Cacheable(cacheNames = "news", key = "'all'")
    public List<NewsResponseTo> getAllNews() {
        return newsRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "news", key = "#id")
    public NewsResponseTo getNewsById(Long id) {
        return newsRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("News not found", 40401));
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "news", key = "'all'"),
            @CacheEvict(cacheNames = "markers", key = "'all'", condition = "#request.markers != null && !#request.markers.isEmpty()")
    })
    public NewsResponseTo createNews(NewsRequestTo request) {
        if (!userRepository.existsById(request.getUserId())) {
            throw new EntityNotFoundException("User not found", 40401);
        }

        List<Marker> markers = resolveMarkers(request.getMarkers());

        News news = mapper.toEntity(request);
        news.setMarkers(markers);
        News saved = newsRepository.save(news);
        return mapper.toDto(saved);
    }

    @Caching(put = {
            @CachePut(cacheNames = "news", key = "#id")
    }, evict = {
            @CacheEvict(cacheNames = "news", key = "'all'"),
            @CacheEvict(cacheNames = "newsUser", key = "#id"),
            @CacheEvict(cacheNames = "markers", key = "'all'", condition = "#request.markers != null && !#request.markers.isEmpty()")
    })
    public NewsResponseTo updateNews(Long id, NewsRequestTo request) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("News not found", 40401));

        List<Marker> markers = resolveMarkers(request.getMarkers());

        News updated = mapper.updateEntity(request, existing);
        updated.setId(id);
        updated.setMarkers(markers);
        News saved = newsRepository.save(updated);
        return mapper.toDto(saved);
    }

    private List<Marker> resolveMarkers(List<String> markerNames) {
        if (markerNames == null || markerNames.isEmpty()) {
            return new ArrayList<>();
        }

        return markerNames.stream().map(name -> {
            Marker newMarker = new Marker();
            newMarker.setName(name);
            return markerRepository.save(newMarker);
        }).collect(Collectors.toList());
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "news", key = "#id"),
            @CacheEvict(cacheNames = "news", key = "'all'"),
            @CacheEvict(cacheNames = "newsUser", key = "#id"),
            @CacheEvict(cacheNames = "markers", key = "'all'")
    })
    public void deleteNews(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new EntityNotFoundException("News not found", 40401);
        }

        News news = newsRepository.findById(id).get();

        news.getMarkers().forEach(marker -> markerRepository.deleteById(marker.getId()));

        newsRepository.deleteById(id);
    }

    @Cacheable(cacheNames = "newsUser", key = "#newsId")
    public UserResponseTo getUserByNewsId(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new EntityNotFoundException("News not found", 40401));

        User user = userRepository.findById(news.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found", 40401));

        return userMapper.toDto(user);
    }
}
