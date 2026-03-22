package com.example.lab.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.lab.dto.NewsRequestTo;
import com.example.lab.dto.NewsResponseTo;
import com.example.lab.dto.UserResponseTo;
import com.example.lab.exception.EntityNotFoundException;
import com.example.lab.mapper.NewsMapper;
import com.example.lab.mapper.UserMapper;
import com.example.lab.model.Marker;
import com.example.lab.model.News;
import com.example.lab.model.User;
import com.example.lab.repository.MarkerRepository;
import com.example.lab.repository.NewsRepository;
import com.example.lab.repository.UserRepository;

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

    public List<NewsResponseTo> getAllNews() {
        return newsRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public NewsResponseTo getNewsById(Long id) {
        return newsRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("News not found", 40401));
    }

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

    public void deleteNews(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new EntityNotFoundException("News not found", 40401);
        }

        News news = newsRepository.findById(id).get();

        news.getMarkers().forEach(marker -> markerRepository.deleteById(marker.getId()));

        newsRepository.deleteById(id);
    }

    public UserResponseTo getUserByNewsId(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new EntityNotFoundException("News not found", 40401));

        User user = userRepository.findById(news.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found", 40401));

        return userMapper.toDto(user);
    }
}
