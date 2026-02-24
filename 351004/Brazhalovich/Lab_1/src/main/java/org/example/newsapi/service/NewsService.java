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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final MarkerRepository markerRepository;
    private final NewsMapper newsMapper;

    @Transactional
    public NewsResponseTo create(NewsRequestTo request) {
        // 1. Проверка юзера (для возврата 403, если его нет)
        if (request.getUserId() == null || !userRepository.existsById(request.getUserId())) {
            throw new org.example.newsapi.exception.NotFoundException("User not found");
        }

        // 2. Проверка уникальности заголовка (предотвращает дубликаты)
        if (newsRepository.existsByTitle(request.getTitle())) {
            throw new org.example.newsapi.exception.AlreadyExistsException("Title exists");
        }

        News news = newsMapper.toEntity(request);
        news.setUser(userRepository.getReferenceById(request.getUserId()));

        // Принудительно ставим время
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        news.setCreated(now);
        news.setModified(now);

        // 3. ПРИВЯЗКА МАРКЕРОВ
        // Если наши "умные" сеттеры сработали, markerIds не будет пустым
        if (request.getMarkerIds() != null && !request.getMarkerIds().isEmpty()) {
            java.util.List<org.example.newsapi.entity.Marker> markers =
                    markerRepository.findAllById(request.getMarkerIds());
            news.setMarkers(new java.util.HashSet<>(markers));
        }

        News saved = newsRepository.save(news);
        return newsMapper.toDto(saved);
    }


    public Page<NewsResponseTo> findAll(Pageable pageable) {
        return newsRepository.findAll(pageable).map(newsMapper::toDto);
    }

    public NewsResponseTo findById(Long id) {
        return newsRepository.findById(id)
                .map(newsMapper::toDto)
                .orElseThrow(() -> new NotFoundException("News not found"));
    }

    @Transactional
    public NewsResponseTo update(Long id, NewsRequestTo request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found"));

        if (!userRepository.existsById(request.getUserId())) {
            throw new NotFoundException("User not found");
        }

        newsMapper.updateEntityFromDto(request, news);
        news.setUser(userRepository.getReferenceById(request.getUserId()));
        news.setModified(LocalDateTime.now());

        if (request.getMarkerIds() != null) {
            List<Marker> markers = markerRepository.findAllById(request.getMarkerIds());
            news.setMarkers(new HashSet<>(markers));
        }

        return newsMapper.toDto(newsRepository.save(news));
    }

    @Transactional
    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new NotFoundException("News not found");
        }
        newsRepository.deleteById(id);
    }
}