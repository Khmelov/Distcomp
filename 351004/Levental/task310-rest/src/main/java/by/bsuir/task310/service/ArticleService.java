package by.bsuir.task310.service;

import by.bsuir.task310.domain.Article;
import by.bsuir.task310.dto.request.ArticleRequestTo;
import by.bsuir.task310.dto.response.ArticleResponseTo;
import by.bsuir.task310.mapper.ArticleMapper;
import by.bsuir.task310.repository.CreatorRepository;
import by.bsuir.task310.repository.ArticleRepository;
import by.bsuir.task310.repository.StickerRepository;
import by.bsuir.task310.web.error.NotFoundException;
import by.bsuir.task310.web.error.ValidationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository repo;
    private final CreatorRepository creatorRepository;
    private final StickerRepository stickerRepository;
    private final ArticleMapper mapper;

    public ArticleService(ArticleRepository repo,
                          CreatorRepository creatorRepository,
                          StickerRepository stickerRepository,
                          ArticleMapper mapper) {
        this.repo = repo;
        this.creatorRepository = creatorRepository;
        this.stickerRepository = stickerRepository;
        this.mapper = mapper;
    }

    public ArticleResponseTo create(ArticleRequestTo dto) {
        validateRelations(dto);

        Article entity = mapper.toEntity(dto);
        Instant now = Instant.now();
        entity.setCreated(now);
        entity.setModified(now);

        Article saved = repo.save(entity);
        return mapper.toResponse(saved);
    }

    public List<ArticleResponseTo> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ArticleResponseTo getById(Long id) {
        Article entity = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("article not found", "01"));
        return mapper.toResponse(entity);
    }

    public ArticleResponseTo update(ArticleRequestTo dto) {
        if (dto.getId() == null) {
            throw new ValidationException("id is required", "01");
        }

        Article existing = repo.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("article not found", "02"));

        validateRelations(dto);

        Article entity = mapper.toEntity(dto);
        entity.setCreated(existing.getCreated());
        entity.setModified(Instant.now());

        Article updated = repo.update(dto.getId(), entity);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("article not found", "03");
        }
        repo.delete(id);
    }

    private void validateRelations(ArticleRequestTo dto) {
        if (!creatorRepository.existsById(dto.getCreatorId())) {
            throw new ValidationException("creator does not exist", "02");
        }

        if (dto.getStickerIds() != null) {
            for (Long stickerId : dto.getStickerIds()) {
                if (!stickerRepository.existsById(stickerId)) {
                    throw new ValidationException("sticker does not exist", "03");
                }
            }
        }
    }
}