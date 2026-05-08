package org.example.newsapi.service;

import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.MarkerRequestTo;
import org.example.newsapi.dto.response.MarkerResponseTo;
import org.example.newsapi.entity.Marker;
import org.example.newsapi.exception.AlreadyExistsException;
import org.example.newsapi.exception.NotFoundException;
import org.example.newsapi.mapper.MarkerMapper;
import org.example.newsapi.repository.MarkerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarkerService {

    private final MarkerRepository markerRepository;
    private final MarkerMapper markerMapper;

    @Transactional
    public MarkerResponseTo create(MarkerRequestTo request) {
        System.out.println(">>> ATTEMPTING TO CREATE MARKER WITH NAME: " + request.getName());

        if (markerRepository.existsByName(request.getName())) {
            System.out.println(">>> MARKER ALREADY EXISTS: " + request.getName());
            throw new AlreadyExistsException("Marker already exists");
        }

        Marker marker = markerMapper.toEntity(request);
        Marker saved = markerRepository.save(marker);

        System.out.println(">>> SUCCESSFULLY SAVED MARKER: " + saved.getName() + " WITH ID: " + saved.getId());

        return markerMapper.toDto(saved);
    }

    public Page<MarkerResponseTo> findAll(Pageable pageable) {
        return markerRepository.findAll(pageable)
                .map(markerMapper::toDto);
    }

    public MarkerResponseTo findById(Long id) {
        return markerRepository.findById(id)
                .map(markerMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Marker not found with id: " + id));
    }

    @Transactional
    public MarkerResponseTo update(Long id, MarkerRequestTo request) {
        Marker marker = markerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Marker not found with id: " + id));

        markerMapper.updateEntityFromDto(request, marker);
        return markerMapper.toDto(markerRepository.save(marker));
    }

    @Transactional
    public void delete(Long id) {
        if (!markerRepository.existsById(id)) {
            throw new NotFoundException("Marker not found with id: " + id);
        }
        markerRepository.deleteById(id);
    }
}