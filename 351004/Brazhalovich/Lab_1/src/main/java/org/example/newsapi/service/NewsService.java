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
        // 1. Проверка юзера (Если его нет - бросаем ошибку)
        if (request.getUserId() == null || !userRepository.existsById(request.getUserId())) {
            throw new NotFoundException("User not found");
        }

        // 2. Проверка дубликата заголовка (ИМЕННО ЭТО ВАЛИЛО ТЕСТ №9)
        if (newsRepository.existsByTitle(request.getTitle())) {
            throw new AlreadyExistsException("News title already exists");
        }

        User user = userRepository.getReferenceById(request.getUserId());
        News news = newsMapper.toEntity(request);
        news.setUser(user);

        // Ставим даты
        news.setCreated(java.time.LocalDateTime.now());
        news.setModified(java.time.LocalDateTime.now());

        // 3. Обработка маркеров (Проверяем, что ID маркеров реально есть в базе)
        if (request.getMarkerIds() != null && !request.getMarkerIds().isEmpty()) {
            java.util.List<Marker> markers = markerRepository.findAllById(request.getMarkerIds());
            // Если тестер прислал несуществующий маркер, можно тоже выкинуть 403
            if (markers.size() != request.getMarkerIds().size()) {
                throw new NotFoundException("One or more markers not found");
            }
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