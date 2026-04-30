package by.tracker.rest_api.service;

import by.tracker.rest_api.dto.MarkerRequestTo;
import by.tracker.rest_api.dto.MarkerResponseTo;
import by.tracker.rest_api.model.Marker;
import by.tracker.rest_api.repository.CrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkerService {

    private final CrudRepository<Marker, Long> repository;
    private final MarkerMapper mapper;

    public MarkerResponseTo create(MarkerRequestTo request) {
        Marker entity = mapper.toEntity(request);
        Marker saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public List<MarkerResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public MarkerResponseTo getById(Long id) {
        Marker entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Marker not found with id: " + id));
        return mapper.toResponse(entity);
    }

    public MarkerResponseTo update(MarkerRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("ID is required for update");
        }
        Marker entity = mapper.toEntity(request);
        entity.setId(request.getId());
        Marker updated = repository.update(entity);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}