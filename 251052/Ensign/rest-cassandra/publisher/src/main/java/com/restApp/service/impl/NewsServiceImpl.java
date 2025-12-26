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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final AuthorRepository authorRepository;
    private final MarkRepository markRepository;
    private final NewsMapper newsMapper;
    private final com.restApp.client.DiscussionClient discussionClient;

    public NewsServiceImpl(NewsRepository newsRepository, AuthorRepository authorRepository,
            MarkRepository markRepository, NewsMapper newsMapper,
            com.restApp.client.DiscussionClient discussionClient) {
        this.newsRepository = newsRepository;
        this.authorRepository = authorRepository;
        this.markRepository = markRepository;
        this.newsMapper = newsMapper;
        this.discussionClient = discussionClient;
    }

    @Override
    public NewsResponseTo create(NewsRequestTo request) {
        if (newsRepository.existsByTitle(request.getTitle())) {
            throw new BusinessException("Title already exists", "40302");
        }

        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new BusinessException("Author not found", "40401"));

        News news = newsMapper.toEntity(request);
        news.setAuthor(author);

        List<Mark> marks = new ArrayList<>();
        if (request.getMarkIds() != null) {
            for (Long markId : request.getMarkIds()) {
                Mark mark = markRepository.findById(markId)
                        .orElseThrow(() -> new BusinessException("Mark not found ID: " + markId, "40402"));
                marks.add(mark);
            }
        }

        if (request.getMarks() != null) {
            for (String markName : request.getMarks()) {
                Mark mark = markRepository.findByName(markName)
                        .orElseGet(() -> {
                            Mark newMark = new Mark();
                            newMark.setName(markName);
                            return markRepository.save(newMark);
                        });
                // Avoid duplicates if both ID and Name provided point to same, or duplicate
                // names
                if (!marks.contains(mark)) {
                    marks.add(mark);
                }
            }
        }

        if (!marks.isEmpty()) {
            news.setMarks(marks);
        }

        News savedNews = newsRepository.save(news);
        return newsMapper.toResponse(savedNews);
    }

    @Override
    public NewsResponseTo update(Long id, NewsRequestTo request) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new BusinessException("News not found", "40403"));

        if (!news.getTitle().equals(request.getTitle()) && newsRepository.existsByTitle(request.getTitle())) {
            throw new BusinessException("Title already exists", "40302");
        }

        // Validate author if changed
        if (request.getAuthorId() != null
                && (news.getAuthor() == null || !request.getAuthorId().equals(news.getAuthor().getId()))) {
            Author newAuthor = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new BusinessException("Author not found", "40401"));
            news.setAuthor(newAuthor);
        }

        newsMapper.updateEntity(news, request);

        // Update marks if provided
        // Update marks if provided (either IDs or Objects)
        if (request.getMarkIds() != null || request.getMarks() != null) {
            List<Mark> newMarks = new ArrayList<>();

            if (request.getMarkIds() != null) {
                for (Long markId : request.getMarkIds()) {
                    Mark mark = markRepository.findById(markId)
                            .orElseThrow(() -> new BusinessException("Mark not found ID: " + markId, "40402"));
                    newMarks.add(mark);
                }
            }

            if (request.getMarks() != null) {
                for (String markName : request.getMarks()) {
                    Mark mark = markRepository.findByName(markName)
                            .orElseGet(() -> {
                                Mark newMark = new Mark();
                                newMark.setName(markName);
                                return markRepository.save(newMark);
                            });
                    if (!newMarks.contains(mark)) {
                        newMarks.add(mark);
                    }
                }
            }

            news.setMarks(newMarks);
        }

        return newsMapper.toResponse(newsRepository.save(news));
    }

    @Override
    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new BusinessException("News not found", "40403");
        }
        newsRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsResponseTo findById(Long id) {
        NewsResponseTo response = newsRepository.findById(id)
                .map(newsMapper::toResponse)
                .orElseThrow(() -> new BusinessException("News not found", "40403"));
        response.setComments(discussionClient.getCommentsByNewsId(id));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsResponseTo> findAll(Pageable pageable) {
        return newsRepository.findAll(pageable)
                .map(news -> {
                    NewsResponseTo response = newsMapper.toResponse(news);
                    response.setComments(discussionClient.getCommentsByNewsId(news.getId()));
                    return response;
                });
    }
}
