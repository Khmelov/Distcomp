package com.example.news.service;

import com.example.common.dto.ArticleRequestTo;
import com.example.common.dto.ArticleResponseTo;
import com.example.common.dto.MarkerResponseTo;
import com.example.common.dto.MessageResponseTo;
import com.example.news.client.DiscussionClient;
import com.example.news.entity.Article;
import com.example.news.entity.Marker;
import com.example.news.exception.EntityNotFoundException;
import com.example.news.exception.ForbiddenException;
import com.example.news.mapper.ArticleMapper;
import com.example.news.repository.ArticleRepository;
import com.example.news.repository.MarkerRepository;
import com.example.news.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final WriterRepository writerRepository;
    private final MarkerRepository markerRepository;
    private final DiscussionClient discussionClient;
    private final ArticleMapper articleMapper;

    public List<ArticleResponseTo> findAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Article> articlePage = articleRepository.findAll(pageable);

        return articlePage.getContent().stream()
                .map(articleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ArticleResponseTo create(ArticleRequestTo request) {
        if (articleRepository.existsByTitle(request.title())) {
            throw new ForbiddenException("Article with this title already exists");
        }
        Article article = articleMapper.toEntity(request);

        if (article.getMarkers() == null) {
            article.setMarkers(new ArrayList<>());
        }

        article.setWriter(writerRepository.findById(request.writerId())
                .orElseThrow(() -> new EntityNotFoundException("Writer not found", "666")));

        if (request.markerIds() != null && !request.markerIds().isEmpty()) {
            List<Marker> foundMarkers = markerRepository.findAllById(request.markerIds());

            article.getMarkers().addAll(foundMarkers);
        }

        Article savedArticle = articleRepository.saveAndFlush(article);

        return articleMapper.toResponse(savedArticle);
    }

//    public ArticleResponseTo findById(Long id) {
//        return articleRepository.findById(id)
//                .map(articleMapper::toResponse)
//                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id, "40401"));
//    }

    public ArticleResponseTo findById(Long id) {
        // 1. Берем базовые данные статьи из Postgres через маппер
        ArticleResponseTo baseResponse = articleRepository.findById(id)
                .map(articleMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id, "40401"));

        // 2. Идем в сервис Discussion за сообщениями (Cassandra)
        List<MessageResponseTo> messages;
        try {
            messages = discussionClient.getMessagesByArticleId(id);
        } catch (Exception e) {
            messages = List.of(); // Если сервис упал, возвращаем пустой список
        }

        // 3. Создаем НОВЫЙ рекорд, копируя данные и добавляя сообщения
        return new ArticleResponseTo(
                baseResponse.id(),
                baseResponse.writerId(),
                baseResponse.title(),
                baseResponse.content(),
                baseResponse.created(),
                baseResponse.modified(),
                baseResponse.markerIds(),
                messages // <-- Передаем полученные сообщения в конструктор
        );
    }

    @Transactional
    public ArticleResponseTo update(Long id, ArticleRequestTo request) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        var writer = writerRepository.findById(request.writerId())
                .orElseThrow(() -> new RuntimeException("Writer not found"));

        existingArticle.setTitle(request.title());
        existingArticle.setContent(request.content());
        existingArticle.setWriter(writer);

        return articleMapper.toResponse(articleRepository.save(existingArticle));
    }

    @Transactional
    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new EntityNotFoundException("Article not found with id: " + id, "40401");
        }
        articleRepository.deleteById(id);
    }

    public List<MarkerResponseTo> getMarkersByArticleId(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found", "40401"));

        return article.getMarkers().stream()
                .map(marker -> new MarkerResponseTo(marker.getId(), marker.getName()))
                .collect(Collectors.toList());
    }

//    public List<MessageResponseTo> getMessagesByArticleId(Long id) {
//        Article article = articleRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Article not found", "40401"));
//
//        return article.getMessages().stream()
//                .map(msg -> new MessageResponseTo(msg.getId(), article.getId(), msg.getContent()))
//                .collect(Collectors.toList());
//    }

    public List<MessageResponseTo> getMessagesByArticleId(Long id) {
        // Проверяем, существует ли статья вообще
        if (!articleRepository.existsById(id)) {
            throw new EntityNotFoundException("Article not found", "40401");
        }

        // Просто перенаправляем запрос в микросервис обсуждений
        try {
            return discussionClient.getMessagesByArticleId(id);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}