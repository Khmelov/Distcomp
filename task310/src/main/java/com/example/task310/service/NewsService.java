package com.example.task310.service;

import com.example.task310.domain.News;
import com.example.task310.dto.request.NewsRequestTo;
import com.example.task310.dto.response.NewsResponseTo;
import com.example.task310.error.BadRequestException;
import com.example.task310.error.NotFoundException;
import com.example.task310.repo.NewsRepo;
import com.example.task310.repo.WriterRepo;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class NewsService {

    private final NewsRepo newsRepo;
    private final WriterRepo writerRepo;

    public NewsService(NewsRepo newsRepo, WriterRepo writerRepo) {
        this.newsRepo = newsRepo;
        this.writerRepo = writerRepo;
    }

    public NewsResponseTo create(NewsRequestTo r) {
        if (!writerRepo.exists(r.writerId())) {
            throw new BadRequestException("Writer does not exist: " + r.writerId());
        }
        var now = OffsetDateTime.now();
        var created = newsRepo.create(new News(null, r.writerId(), r.title(), r.content(), now, now));
        return toDto(created);
    }

    public java.util.List<NewsResponseTo> getAll() {
        return newsRepo.findAll().stream().map(this::toDto).toList();
    }

    public NewsResponseTo getById(long id) {
        var n = newsRepo.find(id).orElseThrow(() -> new NotFoundException("News not found: " + id));
        return toDto(n);
    }

    public NewsResponseTo update(long id, NewsRequestTo r) {
        var existing = newsRepo.find(id).orElseThrow(() -> new NotFoundException("News not found: " + id));
        if (!writerRepo.exists(r.writerId())) {
            throw new BadRequestException("Writer does not exist: " + r.writerId());
        }
        var updated = newsRepo.update(id, new News(
                id, r.writerId(), r.title(), r.content(),
                existing.created(), OffsetDateTime.now()
        ));
        return toDto(updated);
    }

    public void delete(long id) {
        if (!newsRepo.exists(id)) throw new NotFoundException("News not found: " + id);
        newsRepo.delete(id);
    }

    private NewsResponseTo toDto(News n) {
        return new NewsResponseTo(n.id(), n.writerId(), n.title(), n.content(), n.created(), n.modified());
    }
}
