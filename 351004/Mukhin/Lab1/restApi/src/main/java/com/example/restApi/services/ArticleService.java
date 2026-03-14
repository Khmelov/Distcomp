package com.example.restApi.services;

import com.example.restApi.dto.request.ArticleRequestTo;
import com.example.restApi.dto.response.ArticleResponseTo;
import com.example.restApi.exception.NotFoundException;
import com.example.restApi.model.Article;
import com.example.restApi.model.Marker;
import com.example.restApi.model.User;
import com.example.restApi.repository.ArticleRepository;
import com.example.restApi.repository.MarkerRepository;
import com.example.restApi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final MarkerRepository markerRepository;

    public ArticleService(ArticleRepository articleRepository,
                          UserRepository userRepository,
                          MarkerRepository markerRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.markerRepository = markerRepository;
    }

    public Page<ArticleResponseTo> getAll(int page, int size, String sortParam) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortParam));
        return articleRepository.findAll(pageable)
                .map(this::convertToResponseDto);
    }

    public ArticleResponseTo getById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Article not found with id: " + id));
        return convertToResponseDto(article);
    }

    @Transactional
    public ArticleResponseTo create(ArticleRequestTo request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getUserId()));

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setUser(user);

        Set<Marker> markers = new HashSet<>();

        for (String color : new String[]{"red", "green", "blue"}) {
            Marker marker = new Marker();
            marker.setName(color + request.getUserId());
            markers.add(markerRepository.save(marker));
        }

        if (request.getMarkerIds() != null && !request.getMarkerIds().isEmpty()) {
            markers.addAll(markerRepository.findAllById(request.getMarkerIds()));
        }

        article.setMarkers(markers);

        Article saved = articleRepository.save(article);
        return convertToResponseDto(saved);
    }

    @Transactional
    public ArticleResponseTo update(Long id, ArticleRequestTo request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Article not found with id: " + id));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getUserId()));

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setUser(user);
        article.setModified(LocalDateTime.now());

        if (request.getMarkerIds() != null && !request.getMarkerIds().isEmpty()) {
            List<Marker> markers = markerRepository.findAllById(request.getMarkerIds());
            article.setMarkers(new HashSet<>(markers));
        } else {
            article.getMarkers().clear();
        }

        Article updated = articleRepository.save(article);
        return convertToResponseDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Article not found with id: " + id));

        Set<Marker> markers = new HashSet<>(article.getMarkers());
        article.getMarkers().clear();
        articleRepository.save(article);
        markerRepository.deleteAll(markers);

        articleRepository.deleteById(id);
    }

    public Page<ArticleResponseTo> getByUserId(Long userId, int page, int size, String sortParam) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortParam));
        return articleRepository.findByUser_Id(userId, pageable)
                .map(this::convertToResponseDto);
    }

    public Page<ArticleResponseTo> getByMarkerId(Long markerId, int page, int size, String sortParam) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortParam));
        return articleRepository.findByMarkers_Id(markerId, pageable)
                .map(this::convertToResponseDto);
    }

    private ArticleResponseTo convertToResponseDto(Article article) {
        ArticleResponseTo dto = new ArticleResponseTo();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());

        if (article.getUser() != null) {
            dto.setUserId(article.getUser().getId());
        }

        if (article.getMarkers() != null) {
            Set<Long> markerIds = article.getMarkers().stream()
                    .map(Marker::getId)
                    .collect(Collectors.toSet());
            dto.setMarkerIds(markerIds);
        }

        dto.setCreated(article.getCreated());
        dto.setModified(article.getModified());
        return dto;
    }
}