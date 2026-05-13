package com.example.task340.service.impl;

import com.example.task340.domain.dto.request.MarkerRequestTo;
import com.example.task340.domain.dto.response.MarkerResponseTo;
import com.example.task340.domain.entity.Marker;
import com.example.task340.exception.EntityNotFoundException;
import com.example.task340.mapper.MarkerMapper;
import com.example.task340.repository.MarkerRepository;
import com.example.task340.service.MarkerService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkerServiceImpl implements MarkerService {

    private final MarkerRepository markerRepository;
    private final MarkerMapper markerMapper;

    @Override
    public MarkerResponseTo create(MarkerRequestTo request) {
        Marker marker = markerMapper.toEntity(request);
        return markerMapper.toResponse(markerRepository.save(marker));
    }

    @Override
    public List<MarkerResponseTo> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return markerRepository.findAll(pageable).getContent().stream()
                .map(markerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MarkerResponseTo findById(Long id) {
        Marker marker = markerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found with this id"));
        return markerMapper.toResponse(marker);
    }

    @Override
    public MarkerResponseTo update(MarkerRequestTo request) {
        if (!markerRepository.existsById(request.getId())) {
            throw new RuntimeException("Cannot update: Marker not found");
        }
        Marker marker = markerMapper.toEntity(request);
        return markerMapper.toResponse(markerRepository.save(marker));
    }

    @Override
    public void deleteById(Long id) {
        if (!markerRepository.existsById(id)) {
            throw new EntityNotFoundException("Marker not found with id: " + id);
        }
        markerRepository.deleteById(id);
    }
}