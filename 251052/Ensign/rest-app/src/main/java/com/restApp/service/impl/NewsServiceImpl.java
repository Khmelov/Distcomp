package com.restApp.service.impl;

import com.restApp.dto.NewsRequestTo;
import com.restApp.dto.NewsResponseTo;
import com.restApp.exception.BusinessException;
import com.restApp.mapper.NewsMapper;
import com.restApp.model.Author;
import com.restApp.model.Mark;
import com.restApp.model.News;
import com.restApp.repository.AuthorRepository;
import com.restApp.repository.MarkRepository;
import com.restApp.repository.NewsRepository;
import com.restApp.service.NewsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final AuthorRepository authorRepository;
    private final MarkRepository markRepository;
    private final NewsMapper newsMapper;

    public NewsServiceImpl(NewsRepository newsRepository, AuthorRepository authorRepository,
            MarkRepository markRepository, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.authorRepository = authorRepository;
        this.markRepository = markRepository;
        this.newsMapper = newsMapper;
    }

    @Override
    public NewsResponseTo create(NewsRequestTo request) {
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new BusinessException("Author not found", "40401"));

        News news = newsMapper.toEntity(request);
        news.setTimestamp(Instant.now());
        news.setAuthorId(author.getId());

        List<Mark> marks = new ArrayList<>();
        if (request.getMarkIds() != null) {
            for (Long markId : request.getMarkIds()) {
                Mark mark = markRepository.findById(markId)
                        .orElseThrow(() -> new BusinessException("Mark not found ID: " + markId, "40402"));
                marks.add(mark);
            }
        }
        news.setMarks(marks);

        News savedNews = newsRepository.save(news);

        // Maintain relationships
        author.getNews().add(savedNews);
        for (Mark mark : marks) {
            mark.getNews().add(savedNews);
        }

        return newsMapper.toResponse(savedNews);
    }

    @Override
    public NewsResponseTo update(Long id, NewsRequestTo request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new BusinessException("News not found", "40403"));

        // Validate author if changed
        if (request.getAuthorId() != null && !request.getAuthorId().equals(news.getAuthorId())) {
            if (!authorRepository.existsById(request.getAuthorId())) {
                throw new BusinessException("Author not found", "40401");
            }
            authorRepository.findById(news.getAuthorId()).ifPresent(oldAuthor -> oldAuthor.getNews().remove(news));
            authorRepository.findById(request.getAuthorId()).ifPresent(newAuthor -> newAuthor.getNews().add(news));
            news.setAuthorId(request.getAuthorId());
        }

        newsMapper.updateEntity(news, request);

        // Update marks if provided
        if (request.getMarkIds() != null) {
            // Clear old marks logic could be complex (remove news from their lists)
            for (Mark oldMark : news.getMarks()) {
                oldMark.getNews().remove(news);
            }
            List<Mark> newMarks = new ArrayList<>();
            for (Long markId : request.getMarkIds()) {
                Mark mark = markRepository.findById(markId)
                        .orElseThrow(() -> new BusinessException("Mark not found ID: " + markId, "40402"));
                newMarks.add(mark);
                mark.getNews().add(news);
            }
            news.setMarks(newMarks);
        }

        return newsMapper.toResponse(newsRepository.save(news));
    }

    @Override
    public void delete(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new BusinessException("News not found", "40403"));

        // Cleanup relations
        authorRepository.findById(news.getAuthorId()).ifPresent(a -> a.getNews().remove(news));
        for (Mark mark : news.getMarks()) {
            mark.getNews().remove(news);
        }

        newsRepository.deleteById(id);
    }

    @Override
    public NewsResponseTo findById(Long id) {
        return newsRepository.findById(id)
                .map(newsMapper::toResponse)
                .orElseThrow(() -> new BusinessException("News not found", "40403"));
    }

    @Override
    public List<NewsResponseTo> findAll() {
        return newsRepository.findAll().stream()
                .map(newsMapper::toResponse)
                .collect(Collectors.toList());
    }
}
