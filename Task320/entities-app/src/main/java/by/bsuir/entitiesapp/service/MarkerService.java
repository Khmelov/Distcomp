package by.bsuir.entitiesapp.service;

import by.bsuir.entitiesapp.dto.MarkerRequestTo;
import by.bsuir.entitiesapp.dto.MarkerResponseTo;
import by.bsuir.entitiesapp.entity.Marker;
import by.bsuir.entitiesapp.exception.BadRequestException;
import by.bsuir.entitiesapp.exception.NotFoundException;
import by.bsuir.entitiesapp.repository.MarkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarkerService {

    private final MarkerRepository repository;

    public MarkerService(MarkerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MarkerResponseTo create(MarkerRequestTo dto) {
        validate(dto);

        Marker marker = new Marker();
        marker.setName(dto.name);

        return toResponse(repository.save(marker));
    }

    public MarkerResponseTo get(Long id) {
        Marker marker = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Marker not found", "40401"));
        return toResponse(marker);
    }

    public List<MarkerResponseTo> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MarkerResponseTo update(Long id, MarkerRequestTo dto) {
        validate(dto);

        Marker marker = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Marker not found", "40401"));

        marker.setName(dto.name);

        return toResponse(repository.save(marker));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Marker not found", "40401");
        }
        repository.deleteById(id);
    }

    private void validate(MarkerRequestTo dto) {
        if (dto.name == null || dto.name.isBlank()) {
            throw new BadRequestException("Invalid fields", "40001");
        }

        // Additional validation rules
        if (dto.name.length() < 2 || dto.name.length() > 32) {
            throw new BadRequestException("Invalid name length", "40001");
        }
    }

    private MarkerResponseTo toResponse(Marker marker) {
        MarkerResponseTo dto = new MarkerResponseTo();
        dto.id = marker.getId();
        dto.name = marker.getName();
        return dto;
    }
}
