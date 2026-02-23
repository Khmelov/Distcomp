package com.example.restApi.services;

import com.example.restApi.dto.request.MarkerRequestTo;
import com.example.restApi.dto.response.MarkerResponseTo;
import com.example.restApi.exception.AlreadyExistsException;
import com.example.restApi.exception.NotFoundException;
import com.example.restApi.mapper.MarkerMapper;
import com.example.restApi.model.Marker;
import com.example.restApi.repository.MarkerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarkerService {

    private final MarkerRepository markerRepository;
    private final MarkerMapper markerMapper;

    public MarkerService(MarkerRepository markerRepository, MarkerMapper markerMapper) {
        this.markerRepository = markerRepository;
        this.markerMapper = markerMapper;
    }

    public List<MarkerResponseTo> getAll() {
        return markerRepository.findAll().stream()
                .map(markerMapper::toDto)
                .collect(Collectors.toList());
    }

    public MarkerResponseTo getById(Long id) {
        Marker marker = markerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Marker not found with id: " + id));
        return markerMapper.toDto(marker);
    }

    public MarkerResponseTo create(MarkerRequestTo request) {
        if (markerRepository.existsByName(request.getName())) {
            throw new AlreadyExistsException("Marker with name '" + request.getName() + "' already exists");
        }
        Marker marker = markerMapper.toEntity(request);
        marker.setId(null);
        Marker saved = markerRepository.save(marker);
        return markerMapper.toDto(saved);
    }

    public MarkerResponseTo update(MarkerRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("Marker ID is required for update");
        }
        Marker marker = markerRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Marker not found with id: " + request.getId()));
        if (markerRepository.existsByNameAndIdNot(request.getName(), request.getId())) {
            throw new AlreadyExistsException("Marker with name '" + request.getName() + "' already exists");
        }
        markerMapper.updateEntity(marker, request);
        Marker updated = markerRepository.save(marker);
        return markerMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!markerRepository.existsById(id)) {
            throw new NotFoundException("Marker not found with id: " + id);
        }
        markerRepository.deleteById(id);
    }
}
