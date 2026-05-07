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
    public NewsResponseTo create(NewsRequestTo request) {
        // 1. Проверяем пользователя
        if (request.getUserId() == null || !userRepository.existsById(request.getUserId())) {
            throw new NotFoundException("User not found");
        }

        // 2. Проверяем уникальность заголовка
        if (newsRepository.existsByTitle(request.getTitle())) {
            throw new AlreadyExistsException("News title already exists");
        }

        // 3. Создаём новость
        User user = userRepository.getReferenceById(request.getUserId());
        News news = newsMapper.toEntity(request);
        news.setUser(user);
        news.setCreated(LocalDateTime.now());
        news.setModified(LocalDateTime.now());

        // 4. Обрабатываем маркеры по именам
        if (request.getMarkerNames() != null && !request.getMarkerNames().isEmpty()) {
            Set<Marker> markers = new HashSet<>();
            for (String name : request.getMarkerNames()) {
                Marker marker = markerRepository.findByName(name)
                        .orElseGet(() -> {
                            // Создаём новый маркер, если не найден
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

    public NewsResponseTo findById(Long id) {
        return newsRepository.findById(id)
                .map(newsMapper::toDto)
                .orElseThrow(() -> new NotFoundException("News not found"));
    }

    @Transactional
    public NewsResponseTo update(Long id, NewsRequestTo request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found"));

        // Проверяем, что пользователь существует
        if (!userRepository.existsById(request.getUserId())) {
            throw new NotFoundException("User not found");
        }

        // Обновляем поля новости (кроме маркеров)
        newsMapper.updateEntityFromDto(request, news);
        news.setUser(userRepository.getReferenceById(request.getUserId()));
        news.setModified(LocalDateTime.now());

        // Обрабатываем маркеры по именам
        if (request.getMarkerNames() != null) {
            Set<Marker> markers = new HashSet<>();
            for (String name : request.getMarkerNames()) {
                Marker marker = markerRepository.findByName(name)
                        .orElseGet(() -> {
                            // Создаём новый маркер, если не найден
                            Marker newMarker = Marker.builder().name(name).build();
                            return markerRepository.save(newMarker);
                        });
                markers.add(marker);
            }
            news.setMarkers(markers);
        } else {
            // Если список имён не передан, можно оставить маркеры без изменений
            // или очистить связь — зависит от требований
            // news.setMarkers(new HashSet<>());
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