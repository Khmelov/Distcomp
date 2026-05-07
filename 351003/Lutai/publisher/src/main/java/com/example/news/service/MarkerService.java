package com.example.news.service;

import com.example.common.dto.MarkerRequestTo;
import com.example.common.dto.MarkerResponseTo;
import com.example.common.exception.*;
import com.example.news.entity.Marker;
import com.example.news.mapper.MarkerMapper;
import com.example.news.repository.MarkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarkerService {

    private final MarkerRepository markerRepository;
    private final MarkerMapper markerMapper;

    public List<MarkerResponseTo> findAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return markerRepository.findAll(pageable).getContent().stream()
                .map(markerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public MarkerResponseTo findById(Long id) {
        return markerRepository.findById(id)
                .map(markerMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found with id: " + id, "40302"));
    }

    @Transactional
    public MarkerResponseTo update(Long id, MarkerRequestTo request) {
        Marker existingMarker = markerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found with id: " + id, "40302"));

        existingMarker.setName(request.name());

        return markerMapper.toResponse(markerRepository.save(existingMarker));
    }

    @Transactional
    public void delete(Long id) {
        if (!markerRepository.existsById(id)) {
            throw new EntityNotFoundException("Marker not found with id: " + id, "40302");
        }
        markerRepository.deleteById(id);
    }

    @Transactional
    public MarkerResponseTo create(MarkerRequestTo request) {
        if (markerRepository.existsByName(request.name())) {
            throw new ForbiddenException("Marker exists");
        }

        Marker marker = markerMapper.toEntity(request);
        return markerMapper.toResponse(markerRepository.saveAndFlush(marker));
    }
}