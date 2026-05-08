package org.example.newsapi.service;

import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.NewsRequestTo;
import org.example.newsapi.dto.response.NewsResponseTo;
import org.example.newsapi.entity.Marker;
import org.example.newsapi.entity.News;
import org.example.newsapi.entity.User;
import org.example.newsapi.exception.AlreadyExistsException;
import org.example.newsapi.exception.NotFoundException;
import org.example.newsapi.mapper.NewsMapper;
import org.example.newsapi.repository.MarkerRepository;
import org.example.newsapi.repository.NewsRepository;
import org.example.newsapi.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final MarkerRepository markerRepository;
    private final NewsMapper newsMapper;

    @Transactional
    @CacheEvict(value = "news", allEntries = true)
    public NewsResponseTo create(NewsRequestTo request) {
        if (request.getUserId() == null || !userRepository.existsById(request.getUserId())) {
            throw new NotFoundException("User not found");
        }
        if (newsRepository.existsByTitle(request.getTitle())) {
            throw new AlreadyExistsException("News title already exists");
        }

        User user = userRepository.getReferenceById(request.getUserId());
        News news = newsMapper.toEntity(request);
        news.setUser(user);
        news.setCreated(LocalDateTime.now());
        news.setModified(LocalDateTime.now());

        if (request.getMarkerNames() != null && !request.getMarkerNames().isEmpty()) {
            Set<Marker> markers = new HashSet<>();
            for (String name : request.getMarkerNames()) {
                Marker marker = markerRepository.findByName(name)
                        .orElseGet(() -> {
                            Marker newMarker = Marker.builder().name(name).build();
                            return markerRepository.save(newMarker);
                        });
                markers.add(marker);
            }
            news.setMarkers(markers);
        }

        News saved = newsRepository.saveAndFlush(news);
        return newsMapper.toDto(saved);
    }

    public Page<NewsResponseTo> findAll(Pageable pageable) {
        return newsRepository.findAll(pageable).map(newsMapper::toDto);
    }

    @Cacheable(value = "news", key = "#id")
    public NewsResponseTo findById(Long id) {
        return newsRepository.findById(id)
                .map(newsMapper::toDto)
                .orElseThrow(() -> new NotFoundException("News not found"));
    }

    @Transactional
    @CacheEvict(value = "news", allEntries = true)
    public NewsResponseTo update(Long id, NewsRequestTo request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found"));

        if (!userRepository.existsById(request.getUserId())) {
            throw new NotFoundException("User not found");
        }

        newsMapper.updateEntityFromDto(request, news);
        news.setUser(userRepository.getReferenceById(request.getUserId()));
        news.setModified(LocalDateTime.now());

        if (request.getMarkerNames() != null) {
            Set<Marker> markers = new HashSet<>();
            for (String name : request.getMarkerNames()) {
                Marker marker = markerRepository.findByName(name)
                        .orElseGet(() -> {
                            Marker newMarker = Marker.builder().name(name).build();
                            return markerRepository.save(newMarker);
                        });
                markers.add(marker);
            }
            news.setMarkers(markers);
        }

        return newsMapper.toDto(newsRepository.save(news));
    }

    @Transactional
    @CacheEvict(value = "news", allEntries = true)
    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new NotFoundException("News not found");
        }
        newsRepository.deleteById(id);
    }
}