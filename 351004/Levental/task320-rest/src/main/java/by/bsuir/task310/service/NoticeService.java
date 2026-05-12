package by.bsuir.task310.service;

import by.bsuir.task310.domain.Notice;
import by.bsuir.task310.dto.request.NoticeRequestTo;
import by.bsuir.task310.dto.response.NoticeResponseTo;
import by.bsuir.task320.mapper.NoticeMapper;
import by.bsuir.task310.repository.ArticleRepository;
import by.bsuir.task310.repository.NoticeRepository;
import by.bsuir.task310.web.error.NotFoundException;
import by.bsuir.task310.web.error.ValidationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    private final NoticeRepository repo;
    private final ArticleRepository articleRepository;
    private final NoticeMapper mapper;

    public NoticeService(NoticeRepository repo, ArticleRepository articleRepository, NoticeMapper mapper) {
        this.repo = repo;
        this.articleRepository = articleRepository;
        this.mapper = mapper;
    }

    public NoticeResponseTo create(NoticeRequestTo dto) {
        if (!articleRepository.existsById(dto.getArticleId())) {
            throw new ValidationException("article does not exist", "02");
        }

        Notice entity = mapper.toEntity(dto);
        Notice saved = repo.save(entity);
        return mapper.toResponse(saved);
    }

    public List<NoticeResponseTo> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public NoticeResponseTo getById(Long id) {
        Notice entity = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("notice not found", "01"));
        return mapper.toResponse(entity);
    }

    public NoticeResponseTo update(NoticeRequestTo dto) {
        if (dto.getId() == null) {
            throw new ValidationException("id is required", "01");
        }

        if (!repo.existsById(dto.getId())) {
            throw new NotFoundException("notice not found", "02");
        }

        if (!articleRepository.existsById(dto.getArticleId())) {
            throw new ValidationException("article does not exist", "03");
        }

        Notice entity = mapper.toEntity(dto);
        Notice updated = repo.update(dto.getId(), entity);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("notice not found", "04");
        }
        repo.delete(id);
    }
}