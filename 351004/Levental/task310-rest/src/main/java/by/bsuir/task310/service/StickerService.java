package by.bsuir.task310.service;

import by.bsuir.task310.domain.Sticker;
import by.bsuir.task310.dto.request.StickerRequestTo;
import by.bsuir.task310.dto.response.StickerResponseTo;
import by.bsuir.task310.mapper.StickerMapper;
import by.bsuir.task310.repository.StickerRepository;
import by.bsuir.task310.web.error.NotFoundException;
import by.bsuir.task310.web.error.ValidationException;
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

    public StickerResponseTo create(StickerRequestTo dto) {
        Sticker entity = mapper.toEntity(dto);
        Sticker saved = repo.save(entity);
        return mapper.toResponse(saved);
    }

    public List<StickerResponseTo> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public StickerResponseTo getById(Long id) {
        Sticker entity = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("sticker not found", "01"));
        return mapper.toResponse(entity);
    }

    public StickerResponseTo update(StickerRequestTo dto) {
        if (dto.getId() == null) {
            throw new ValidationException("id is required", "01");
        }

        if (!repo.existsById(dto.getId())) {
            throw new NotFoundException("sticker not found", "02");
        }

        Sticker entity = mapper.toEntity(dto);
        Sticker updated = repo.update(dto.getId(), entity);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("sticker not found", "03");
        }
        repo.delete(id);
    }
}