package org.example.newsapi.service;


import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.MarkerRequestTo;
import org.example.newsapi.dto.response.MarkerResponseTo;
import org.example.newsapi.entity.Marker;
import org.example.newsapi.exception.NotFoundException;
import org.example.newsapi.mapper.MarkerMapper;
import org.example.newsapi.mapper.MarkerMapper;
import org.example.newsapi.repository.impl.MarkerRepository;
import org.example.newsapi.repository.impl.MarkerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkerService {
    private final MarkerRepository markerRepository;
    private final MarkerMapper markerMapper;

    public MarkerResponseTo create(MarkerRequestTo request) {
        Marker marker = markerMapper.toEntity(request);
        return markerMapper.toDto(markerRepository.save(marker));
    }

    public List<MarkerResponseTo> findAll() {
        return markerRepository.findAll().stream()
                .map(markerMapper::toDto)
                .collect(Collectors.toList());
    }

    public MarkerResponseTo findById(Long id) {
        return markerRepository.findById(id)
                .map(markerMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Marker not found with id: " + id));
    }

    public MarkerResponseTo update(Long id, MarkerRequestTo request) {
        Marker marker = markerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Marker not found with id: " + id));
        markerMapper.updateEntityFromDto(request, marker);
        return markerMapper.toDto(markerRepository.update(marker));
    }

    public void delete(Long id) {
        if (markerRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Marker not found with id: " + id);
        }
        markerRepository.deleteById(id);
    }
}