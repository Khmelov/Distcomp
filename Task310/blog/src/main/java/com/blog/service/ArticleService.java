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
        // Validate that writer exists
        if (!writerRepository.existsById(request.getWriterId())) {
            throw new EntityNotFoundException("Writer not found with id: " + request.getWriterId());
        }

        Article article = articleMapper.requestToToEntity(request);
        Article savedArticle = articleRepository.save(article);
        return articleMapper.entityToResponseTo(savedArticle);
    }

    public ArticleResponseTo update(Long id, ArticleRequestTo request) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));

        // Validate that writer exists
        if (!writerRepository.existsById(request.getWriterId())) {
            throw new EntityNotFoundException("Writer not found with id: " + request.getWriterId());
        }

        articleMapper.updateEntityFromRequest(request, existingArticle);
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
        return articleRepository.findByWriterId(writerId).stream()
                .map(articleMapper::entityToResponseTo)
                .collect(Collectors.toList());
    }
}