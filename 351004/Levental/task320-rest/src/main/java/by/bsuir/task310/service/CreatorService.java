package by.bsuir.task310.service;

import by.bsuir.task310.domain.Creator;
import by.bsuir.task310.dto.request.CreatorRequestTo;
import by.bsuir.task310.dto.response.CreatorResponseTo;
import by.bsuir.task320.mapper.CreatorMapper;
import by.bsuir.task310.repository.CreatorRepository;
import by.bsuir.task310.web.error.NotFoundException;
import by.bsuir.task310.web.error.ValidationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreatorService {

    private final CreatorRepository repo;
    private final CreatorMapper mapper;

    public CreatorService(CreatorRepository repo, CreatorMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public CreatorResponseTo create(CreatorRequestTo dto) {
        Creator entity = mapper.toEntity(dto);
        Creator saved = repo.save(entity);
        return mapper.toResponse(saved);
    }

    public List<CreatorResponseTo> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public CreatorResponseTo getById(Long id) {
        Creator entity = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("creator not found", "01"));
        return mapper.toResponse(entity);
    }

    public CreatorResponseTo update(CreatorRequestTo dto) {
        if (dto.getId() == null) {
            throw new ValidationException("id is required", "01");
        }

        if (!repo.existsById(dto.getId())) {
            throw new NotFoundException("creator not found", "02");
        }

        Creator entity = mapper.toEntity(dto);
        Creator updated = repo.update(dto.getId(), entity);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("creator not found", "03");
        }
        repo.delete(id);
    }
}