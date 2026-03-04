package com.example.restApi.services;

import com.example.restApi.dto.request.MarkerRequestTo;
import com.example.restApi.dto.response.MarkerResponseTo;
import com.example.restApi.exception.NotFoundException;
import com.example.restApi.model.Marker;
import com.example.restApi.repository.MarkerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MarkerService {

    private final MarkerRepository markerRepository;

    public MarkerService(MarkerRepository markerRepository) {
        this.markerRepository = markerRepository;
    }

    public Page<MarkerResponseTo> getAll(int page, int size, String sortParam) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortParam));
        return markerRepository.findAll(pageable)
                .map(this::convertToResponseDto);
    }

    public MarkerResponseTo getById(Long id) {
        Marker marker = markerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Marker not found with id: " + id));
        return convertToResponseDto(marker);
    }

    @Transactional
    public MarkerResponseTo create(MarkerRequestTo request) {
        Marker marker = new Marker();
        marker.setName(request.getName());

        Marker saved = markerRepository.save(marker);
        return convertToResponseDto(saved);
    }

    @Transactional
    public MarkerResponseTo update(Long id, MarkerRequestTo request) {
        Marker marker = markerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Marker not found with id: " + id));

        marker.setName(request.getName());
        marker.setModified(LocalDateTime.now());

        Marker updated = markerRepository.save(marker);
        return convertToResponseDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!markerRepository.existsById(id)) {
            throw new NotFoundException("Marker not found with id: " + id);
        }
        markerRepository.deleteById(id);
    }

    private MarkerResponseTo convertToResponseDto(Marker marker) {
        MarkerResponseTo dto = new MarkerResponseTo();
        dto.setId(marker.getId());
        dto.setName(marker.getName());
        dto.setCreated(marker.getCreated());
        dto.setModified(marker.getModified());
        return dto;
    }
}