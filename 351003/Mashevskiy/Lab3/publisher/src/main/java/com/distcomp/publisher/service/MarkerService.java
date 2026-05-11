package com.distcomp.publisher.service;

import com.distcomp.publisher.dto.MarkerRequestDTO;
import com.distcomp.publisher.dto.MarkerResponseDTO;
import com.distcomp.publisher.model.Marker;
import com.distcomp.publisher.repository.MarkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarkerService {

    @Autowired
    private MarkerRepository markerRepository;

    public List<MarkerResponseDTO> getAll() {
        return markerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MarkerResponseDTO getById(Long id) {
        Marker marker = markerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marker not found with id: " + id));
        return toResponse(marker);
    }

    public MarkerResponseDTO create(MarkerRequestDTO dto) {
        Marker marker = new Marker();
        marker.setName(dto.getName());
        return toResponse(markerRepository.save(marker));
    }

    public MarkerResponseDTO update(Long id, MarkerRequestDTO dto) {
        Marker marker = markerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marker not found with id: " + id));
        marker.setName(dto.getName());
        return toResponse(markerRepository.save(marker));
    }

    public void delete(Long id) {
        if (!markerRepository.existsById(id)) {
            throw new RuntimeException("Marker not found with id: " + id);
        }
        markerRepository.deleteById(id);
    }

    private MarkerResponseDTO toResponse(Marker marker) {
        return new MarkerResponseDTO(marker.getId(), marker.getName());
    }
}