package by.tracker.rest_api.service;

import by.tracker.rest_api.dto.CreatorRequestTo;
import by.tracker.rest_api.dto.CreatorResponseTo;
import by.tracker.rest_api.model.Creator;
import by.tracker.rest_api.repository.CrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreatorService {

    private final CrudRepository<Creator, Long> repository;
    private final CreatorMapper mapper;

    public CreatorResponseTo create(CreatorRequestTo request) {
        Creator entity = mapper.toEntity(request);
        Creator saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public List<CreatorResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public CreatorResponseTo getById(Long id) {
        Creator entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Creator not found with id: " + id));
        return mapper.toResponse(entity);
    }

    public CreatorResponseTo update(CreatorRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("ID is required for update");
        }
        Creator entity = mapper.toEntity(request);
        entity.setId(request.getId());
        Creator updated = repository.update(entity);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}