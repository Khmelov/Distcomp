package com.example.task320.service;

import com.example.task320.domain.NewsEntity;
import com.example.task320.domain.NoticeEntity;
import com.example.task320.dto.request.NoticeRequestTo;
import com.example.task320.dto.response.NoticeResponseTo;
import com.example.task320.error.BadRequestException;
import com.example.task320.error.NotFoundException;
import com.example.task320.repo.NewsRepository;
import com.example.task320.repo.NoticeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepo;
    private final NewsRepository newsRepo;

    public NoticeService(NoticeRepository noticeRepo, NewsRepository newsRepo) {
        this.noticeRepo = noticeRepo;
        this.newsRepo = newsRepo;
    }

    @Transactional
    public NoticeResponseTo create(NoticeRequestTo r) {
        NewsEntity news = newsRepo.findById(r.newsId())
                .orElseThrow(() -> new BadRequestException("News does not exist: " + r.newsId()));

        NoticeEntity n = new NoticeEntity();
        n.setNews(news);
        n.setContent(r.content());
        n = noticeRepo.save(n);
        return toDto(n);
    }

    public List<NoticeResponseTo> getAll() {
        return noticeRepo.findAll().stream().map(this::toDto).toList();
    }

    public NoticeResponseTo getById(long id) {
        return toDto(noticeRepo.findById(id).orElseThrow(() -> new NotFoundException("Notice not found: " + id)));
    }

    @Transactional
    public NoticeResponseTo update(long id, NoticeRequestTo r) {
        NoticeEntity n = noticeRepo.findById(id).orElseThrow(() -> new NotFoundException("Notice not found: " + id));
        NewsEntity news = newsRepo.findById(r.newsId())
                .orElseThrow(() -> new BadRequestException("News does not exist: " + r.newsId()));

        n.setNews(news);
        n.setContent(r.content());
        n = noticeRepo.save(n);
        return toDto(n);
    }

    @Transactional
    public void delete(long id) {
        NoticeEntity n = noticeRepo.findById(id).orElseThrow(() -> new NotFoundException("Notice not found: " + id));
        noticeRepo.delete(n);
    }

    private NoticeResponseTo toDto(NoticeEntity n) {
        return new NoticeResponseTo(n.getId(), n.getNews().getId(), n.getContent());
    }
}
