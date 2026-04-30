package by.tracker.rest_api.service;

import by.tracker.rest_api.dto.MarkerRequestDto;
import by.tracker.rest_api.dto.MarkerResponseDto;
import by.tracker.rest_api.entity.Marker;
import by.tracker.rest_api.exception.DuplicateResourceException;
import by.tracker.rest_api.exception.ResourceNotFoundException;
import by.tracker.rest_api.exception.ValidationException;
import by.tracker.rest_api.mapper.MarkerMapper;
import by.tracker.rest_api.repository.MarkerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MarkerService {

    private final MarkerRepository markerRepository;
    private final MarkerMapper markerMapper;

    // Конструктор вместо @RequiredArgsConstructor
    public MarkerService(MarkerRepository markerRepository, MarkerMapper markerMapper) {
        this.markerRepository = markerRepository;
        this.markerMapper = markerMapper;
    }

    @Transactional(readOnly = true)
    public MarkerResponseDto getById(Long id) {
        Marker marker = markerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Marker not found with id: " + id, 40403));
        return markerMapper.toResponseDto(marker);
    }

    @Transactional(readOnly = true)
    public Page<MarkerResponseDto> getAll(Pageable pageable) {
        return markerRepository.findAll(pageable)
                .map(markerMapper::toResponseDto);
    }

    public MarkerResponseDto create(MarkerRequestDto dto) {
        validateMarker(dto);

        if (markerRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "Marker with name '" + dto.getName() + "' already exists", 40902);
        }

        Marker marker = markerMapper.toEntity(dto);
        marker = markerRepository.save(marker);
        return markerMapper.toResponseDto(marker);
    }

    public MarkerResponseDto update(MarkerRequestDto dto) {
        if (dto.getId() == null) {
            throw new ValidationException("ID is required for update", 40001);
        }

        validateMarker(dto);

        Marker existingMarker = markerRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Marker not found with id: " + dto.getId(), 40403));

        if (!existingMarker.getName().equals(dto.getName()) &&
                markerRepository.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "Marker with name '" + dto.getName() + "' already exists", 40902);
        }

        markerMapper.updateEntity(dto, existingMarker);
        existingMarker = markerRepository.save(existingMarker);
        return markerMapper.toResponseDto(existingMarker);
    }

    public void delete(Long id) {
        if (!markerRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Marker not found with id: " + id, 40403);
        }
        markerRepository.deleteById(id);
    }

    private void validateMarker(MarkerRequestDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ValidationException("Name cannot be empty", 40015);
        }
        if (dto.getName().length() < 2 || dto.getName().length() > 32) {
            throw new ValidationException("Name must be between 2 and 32 characters", 40016);
        }
    }
}