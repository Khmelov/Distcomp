package by.bsuir.task320.service;

import by.bsuir.task320.domain.Sticker;
import by.bsuir.task320.dto.request.StickerRequestTo;
import by.bsuir.task320.dto.response.StickerResponseTo;
import by.bsuir.task320.mapper.StickerMapper;
import by.bsuir.task320.repository.StickerRepository;
import by.bsuir.task320.web.error.ConflictException;
import by.bsuir.task320.web.error.NotFoundException;
import by.bsuir.task320.web.error.ValidationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StickerService {

    private final StickerRepository repo;
    private final StickerMapper mapper;

    public StickerService(StickerRepository repo, StickerMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    // CREATE
    public StickerResponseTo create(StickerRequestTo dto) {
        if (repo.existsByName(dto.getName())) {
            throw new ConflictException("sticker name already exists", "01");
        }

        Sticker entity = mapper.toEntity(dto);
        entity.setId(null); // важно для JPA

        Sticker saved = repo.save(entity);
        return mapper.toResponse(saved);
    }

    // GET ALL
    public List<StickerResponseTo> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    // GET BY ID
    public StickerResponseTo getById(Long id) {
        Sticker entity = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("sticker not found", "01"));
        return mapper.toResponse(entity);
    }

    // UPDATE
    public StickerResponseTo update(StickerRequestTo dto) {
        if (dto.getId() == null) {
            throw new ValidationException("id is required", "01");
        }

        Sticker entity = repo.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("sticker not found", "02"));

        entity.setName(dto.getName());

        Sticker updated = repo.save(entity);
        return mapper.toResponse(updated);
    }

    // DELETE
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("sticker not found", "03");
        }

        repo.deleteById(id);
    }
}