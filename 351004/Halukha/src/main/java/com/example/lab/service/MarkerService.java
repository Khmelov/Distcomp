package com.example.lab.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.lab.dto.MarkerRequestTo;
import com.example.lab.dto.MarkerResponseTo;
import com.example.lab.exception.EntityNotFoundException;
import com.example.lab.mapper.MarkerMapper;
import com.example.lab.model.Marker;
import com.example.lab.repository.MarkerRepository;

@Service
public class MarkerService {

    private final MarkerRepository newsRepository;
    private final MarkerMapper mapper = MarkerMapper.INSTANCE;

    public MarkerService(MarkerRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<MarkerResponseTo> getAllMarker() {
        return newsRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public MarkerResponseTo getMarkerById(Long id) {
        return newsRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found", 40401));
    }

    public MarkerResponseTo createMarker(MarkerRequestTo request) {
        Marker news = mapper.toEntity(request);
        Marker saved = newsRepository.save(news);
        return mapper.toDto(saved);
    }

    public MarkerResponseTo updateMarker(Long id, MarkerRequestTo request) {
        Marker existing = newsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found", 40401));
        Marker updated = mapper.updateEntity(request, existing);
        updated.setId(id);
        Marker saved = newsRepository.save(updated);
        return mapper.toDto(saved);
    }

    public void deleteMarker(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new EntityNotFoundException("Marker not found", 40401);
        }
        newsRepository.deleteById(id);
    }
}
