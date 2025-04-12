package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.mapper.ArticleMapper;
import by.bsuir.distcomp.dto.request.ArticleRequestTo;
import by.bsuir.distcomp.dto.response.ArticleResponseTo;
import by.bsuir.distcomp.entity.Article;
import by.bsuir.distcomp.entity.Marker;
import by.bsuir.distcomp.repository.ArticleRepository;
import by.bsuir.distcomp.repository.MarkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MarkerRepository markerRepository;

    private final ArticleMapper articleMapper;

    public ArticleService(ArticleRepository articleRepository, ArticleMapper articleMapper, MarkerRepository markerRepository) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.markerRepository = markerRepository;
    }

    public List<ArticleResponseTo> getAllArticles() {
        return articleRepository.findAll().stream().map(articleMapper::toDto).toList();
    }

    public ArticleResponseTo getArticleById(Long id) {
        return articleRepository.findById(id)
                .map(articleMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Article with id: " + id + " not found"));
    }

    @Transactional
    public ArticleResponseTo createArticle(ArticleRequestTo articleRequestTo) {
        Article article = articleMapper.toEntity(articleRequestTo);

        List<Marker> markers = markerRepository.saveAll(article.getMarkers());

        return articleMapper.toDto(articleRepository.save(article));
    }

    @Transactional
    public ArticleResponseTo updateArticle(ArticleRequestTo articleRequestTo) {
        getArticleById(articleRequestTo.getId());
        return articleMapper.toDto(articleRepository.save(articleMapper.toEntity(articleRequestTo)));
    }

    @Transactional
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Article with id: " + id + " not found"));

        for (Marker marker : article.getMarkers()) {
            markerRepository.deleteById(marker.getId());
        }

//        getArticleById(id);
        articleRepository.deleteById(id);
    }
    
}
