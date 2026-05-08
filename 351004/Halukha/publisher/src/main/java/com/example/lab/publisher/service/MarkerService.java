package com.example.lab.publisher.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.lab.publisher.dto.MarkerRequestTo;
import com.example.lab.publisher.dto.MarkerResponseTo;
import com.example.lab.publisher.exception.EntityNotFoundException;
import com.example.lab.publisher.mapper.MarkerMapper;
import com.example.lab.publisher.model.Marker;
import com.example.lab.publisher.repository.MarkerRepository;

@Service
public class MarkerService {

    private final MarkerRepository markerRepository;
    private final MarkerMapper mapper = MarkerMapper.INSTANCE;

    public MarkerService(MarkerRepository newsRepository) {
        this.markerRepository = newsRepository;
    }

    public List<MarkerResponseTo> getAllMarker() {
        return markerRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public MarkerResponseTo getMarkerById(Long id) {
        return markerRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found", 40401));
    }

    public MarkerResponseTo createMarker(MarkerRequestTo request) {
        Marker news = mapper.toEntity(request);
        Marker saved = markerRepository.save(news);
        return mapper.toDto(saved);
    }

    public MarkerResponseTo updateMarker(Long id, MarkerRequestTo request) {
        Marker existing = markerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found", 40401));
        Marker updated = mapper.updateEntity(request, existing);
        updated.setId(id);
        Marker saved = markerRepository.save(updated);
        return mapper.toDto(saved);
    }

    public void deleteMarker(Long id) {
        if (!markerRepository.existsById(id)) {
            throw new EntityNotFoundException("Marker not found", 40401);
        }
        markerRepository.deleteById(id);
    }
}
