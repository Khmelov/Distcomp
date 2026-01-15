package com.example.task320.service;

import com.example.task320.domain.NewsEntity;
import com.example.task320.domain.WriterEntity;
import com.example.task320.dto.request.NewsRequestTo;
import com.example.task320.dto.response.NewsResponseTo;
import com.example.task320.error.BadRequestException;
import com.example.task320.error.ForbiddenException;
import com.example.task320.error.NotFoundException;
import com.example.task320.repo.NewsRepository;
import com.example.task320.repo.WriterRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class NewsService {

    private final NewsRepository newsRepo;
    private final WriterRepository writerRepo;

    public NewsService(NewsRepository newsRepo, WriterRepository writerRepo) {
        this.newsRepo = newsRepo;
        this.writerRepo = writerRepo;
    }

    @Transactional
    public NewsResponseTo create(NewsRequestTo r) {
        WriterEntity writer = writerRepo.findById(r.writerId())
                .orElseThrow(() -> new BadRequestException("Writer does not exist: " + r.writerId()));

        if (newsRepo.existsByTitle(r.title())) {
            throw new ForbiddenException("Duplicate title");
        }

        NewsEntity n = new NewsEntity();
        n.setWriter(writer);
        n.setTitle(r.title());
        n.setContent(r.content());
        var now = OffsetDateTime.now();
        n.setCreated(now);
        n.setModified(now);

        n = newsRepo.save(n);
        return toDto(n);
    }

    public List<NewsResponseTo> getAll() {
        return newsRepo.findAll().stream().map(this::toDto).toList();
    }

    public NewsResponseTo getById(long id) {
        return toDto(newsRepo.findById(id).orElseThrow(() -> new NotFoundException("News not found: " + id)));
    }

    @Transactional
    public NewsResponseTo update(long id, NewsRequestTo r) {
        NewsEntity n = newsRepo.findById(id).orElseThrow(() -> new NotFoundException("News not found: " + id));

        WriterEntity writer = writerRepo.findById(r.writerId())
                .orElseThrow(() -> new BadRequestException("Writer does not exist: " + r.writerId()));

        if (!n.getTitle().equals(r.title()) && newsRepo.existsByTitle(r.title())) {
            throw new ForbiddenException("Duplicate title");
        }

        n.setWriter(writer);
        n.setTitle(r.title());
        n.setContent(r.content());
        n.setModified(OffsetDateTime.now());

        n = newsRepo.save(n);
        return toDto(n);
    }

    @Transactional
    public void delete(long id) {
        NewsEntity n = newsRepo.findById(id).orElseThrow(() -> new NotFoundException("News not found: " + id));
        // чтобы корректно удалились строки join table
        n.getStickers().clear();
        newsRepo.delete(n);
    }

    private NewsResponseTo toDto(NewsEntity n) {
        return new NewsResponseTo(
                n.getId(),
                n.getWriter().getId(),
                n.getTitle(),
                n.getContent(),
                n.getCreated(),
                n.getModified()
        );
    }
}
