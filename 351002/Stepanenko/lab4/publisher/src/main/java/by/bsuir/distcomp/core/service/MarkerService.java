package by.bsuir.distcomp.core.service;

import by.bsuir.distcomp.dto.request.MarkerRequestTo;
import by.bsuir.distcomp.dto.response.MarkerResponseTo;
import by.bsuir.distcomp.core.domain.Marker;
import by.bsuir.distcomp.core.exception.DuplicateException;
import by.bsuir.distcomp.core.exception.ResourceNotFoundException;
import by.bsuir.distcomp.core.mapper.MarkerMapper;
import by.bsuir.distcomp.core.repository.MarkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarkerService {
    private final MarkerRepository markerRepository;
    private final MarkerMapper markerMapper;

    public MarkerService(MarkerRepository markerRepository, MarkerMapper markerMapper) {
        this.markerRepository = markerRepository;
        this.markerMapper = markerMapper;
    }

    public MarkerResponseTo create(MarkerRequestTo dto) {
        if (markerRepository.existsByName(dto.getName())) {
            throw new DuplicateException("Marker name already exists", 40303);
        }
        Marker entity = markerMapper.toEntity(dto);
        entity.setId(null); // Важно: всегда создаем новый
        return markerMapper.toResponseDto(markerRepository.save(entity));
    }

    public MarkerResponseTo getById(Long id) {
        return markerRepository.findById(id)
                .map(markerMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Marker not found", 40412));
    }

    public List<MarkerResponseTo> getAll() {
        return markerRepository.findAll().stream()
                .map(markerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public MarkerResponseTo update(MarkerRequestTo dto) {
        Marker existing = markerRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Marker not found", 40413));

        if (!existing.getName().equals(dto.getName()) && markerRepository.existsByName(dto.getName())) {
            throw new DuplicateException("Marker name already exists", 40304);
        }

        existing.setName(dto.getName());
        return markerMapper.toResponseDto(markerRepository.save(existing));
    }

    public void deleteById(Long id) {
        if (!markerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Marker not found", 40414);
        }
        markerRepository.deleteById(id);
    }
}