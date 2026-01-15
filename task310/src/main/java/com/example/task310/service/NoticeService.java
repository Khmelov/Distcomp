package com.example.task310.service;

import com.example.task310.domain.Notice;
import com.example.task310.dto.request.NoticeRequestTo;
import com.example.task310.dto.response.NoticeResponseTo;
import com.example.task310.error.BadRequestException;
import com.example.task310.error.NotFoundException;
import com.example.task310.repo.NewsRepo;
import com.example.task310.repo.NoticeRepo;
import org.springframework.stereotype.Service;

@Service
public class NoticeService {

    private final NoticeRepo noticeRepo;
    private final NewsRepo newsRepo;

    public NoticeService(NoticeRepo noticeRepo, NewsRepo newsRepo) {
        this.noticeRepo = noticeRepo;
        this.newsRepo = newsRepo;
    }

    public NoticeResponseTo create(NoticeRequestTo r) {
        if (!newsRepo.exists(r.newsId())) {
            throw new BadRequestException("News does not exist: " + r.newsId());
        }
        var created = noticeRepo.create(new Notice(null, r.newsId(), r.content()));
        return toDto(created);
    }

    public java.util.List<NoticeResponseTo> getAll() {
        return noticeRepo.findAll().stream().map(this::toDto).toList();
    }

    public NoticeResponseTo getById(long id) {
        var n = noticeRepo.find(id).orElseThrow(() -> new NotFoundException("Notice not found: " + id));
        return toDto(n);
    }

    public NoticeResponseTo update(long id, NoticeRequestTo r) {
        if (!noticeRepo.exists(id)) throw new NotFoundException("Notice not found: " + id);
        if (!newsRepo.exists(r.newsId())) throw new BadRequestException("News does not exist: " + r.newsId());
        var updated = noticeRepo.update(id, new Notice(id, r.newsId(), r.content()));
        return toDto(updated);
    }

    public void delete(long id) {
        if (!noticeRepo.exists(id)) throw new NotFoundException("Notice not found: " + id);
        noticeRepo.delete(id);
    }

    private NoticeResponseTo toDto(Notice n) {
        return new NoticeResponseTo(n.id(), n.newsId(), n.content());
    }
}
