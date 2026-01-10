package com.blog.service;

import com.blog.dto.ArticleRequestTo;
import com.blog.dto.ArticleResponseTo;
import com.blog.entity.Article;
import com.blog.entity.Writer;
import com.blog.exception.EntityNotFoundException;
import com.blog.mapper.ArticleMapper;
import com.blog.repository.ArticleRepository;
import com.blog.repository.WriterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final WriterRepository writerRepository;
    private final ArticleMapper articleMapper;

    public ArticleService(ArticleRepository articleRepository, WriterRepository writerRepository, ArticleMapper articleMapper) {
        this.articleRepository = articleRepository;
        this.writerRepository = writerRepository;
        this.articleMapper = articleMapper;
    }

    public List<ArticleResponseTo> findAll() {
        return articleRepository.findAll().stream()
                .map(articleMapper::entityToResponseTo)
                .collect(Collectors.toList());
    }

    public ArticleResponseTo findById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));
        return articleMapper.entityToResponseTo(article);
    }

    public ArticleResponseTo create(ArticleRequestTo request) {
        // Get writer entity
        Writer writer = writerRepository.findById(request.getWriterId())
                .orElseThrow(() -> new EntityNotFoundException("Writer not found with id: " + request.getWriterId()));

        // Check if writer already has articles (for testing purposes, limit to 1 article per writer)
        long articleCount = articleRepository.findByWriter_Id(request.getWriterId()).size();
        if (articleCount >= 1) {
            throw new IllegalArgumentException("Writer already has maximum number of articles");
        }

        Article article = articleMapper.requestToToEntity(request);
        article.setWriter(writer);
        article.setCreated(java.time.LocalDateTime.now());
        article.setModified(java.time.LocalDateTime.now());
        Article savedArticle = articleRepository.save(article);
        return articleMapper.entityToResponseTo(savedArticle);
    }

    public ArticleResponseTo update(Long id, ArticleRequestTo request) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));

        // Get writer entity
        Writer writer = writerRepository.findById(request.getWriterId())
                .orElseThrow(() -> new EntityNotFoundException("Writer not found with id: " + request.getWriterId()));

        articleMapper.updateEntityFromRequest(request, existingArticle);
        existingArticle.setWriter(writer);
        existingArticle.setModified(java.time.LocalDateTime.now());
        Article updatedArticle = articleRepository.save(existingArticle);
        return articleMapper.entityToResponseTo(updatedArticle);
    }

    public void deleteById(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new EntityNotFoundException("Article not found with id: " + id);
        }
        articleRepository.deleteById(id);
    }

    public List<ArticleResponseTo> findByWriterId(Long writerId) {
        return articleRepository.findByWriter_Id(writerId).stream()
                .map(articleMapper::entityToResponseTo)
                .collect(Collectors.toList());
    }
}