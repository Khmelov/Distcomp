package com.markerservice.services;

import com.markerservice.configs.exceptionhandlerconfig.exceptions.MarkerAlreadyExistsException;
import com.markerservice.configs.exceptionhandlerconfig.exceptions.MarkerNotFoundException;
import com.markerservice.dtos.MarkerRequestTo;
import com.markerservice.dtos.MarkerResponseByNameTo;
import com.markerservice.dtos.MarkerResponseTo;
import com.markerservice.dtos.TweetMarkersRequestByNameTo;
import com.markerservice.models.Marker;
import com.markerservice.repositories.MarkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkerService {

    private final MarkerRepository markerRepository;

    public MarkerResponseTo createMarker(MarkerRequestTo request) {
        if (markerRepository.findMarkerByName(request.getName()).isPresent()) {
            throw new MarkerAlreadyExistsException("Marker already exists");
        }
        Marker saved = markerRepository.save(toEntity(request));
        return toResponse(saved);
    }

    public List<MarkerResponseTo> findAllMarkers() {
        return markerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public MarkerResponseTo findMarkerById(Long id) {
        Marker marker = markerRepository.findMarkerById(id)
                .orElseThrow(() -> new MarkerNotFoundException("Marker not found"));
        return toResponse(marker);
    }

    public MarkerResponseTo updateMarkerById(MarkerRequestTo request, Long id) {
        Marker marker = markerRepository.findMarkerById(id)
                .orElseThrow(() -> new MarkerNotFoundException("Marker not found"));

        if (markerRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new MarkerAlreadyExistsException("Marker already exists");
        }

        marker.setName(request.getName());
        Marker updated = markerRepository.save(marker);
        return toResponse(updated);
    }

    public void deleteMarkerById(Long id) {
        if (!markerRepository.existsById(id)) {
            throw new MarkerNotFoundException("Marker not found");
        }
        markerRepository.deleteById(id);
    }

    public void deleteMarkersByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        markerRepository.deleteAllByIdInBatch(ids);
    }

    public List<MarkerResponseTo> findAllMarkersByIds(List<Long> ids) {
        return markerRepository.findAllByIdIn(ids).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MarkerResponseByNameTo> createValidMarkersByNames(List<TweetMarkersRequestByNameTo> request) {
        if (request == null || request.isEmpty()) {
            return Collections.emptyList();
        }

        List<MarkerResponseByNameTo> response = new ArrayList<>();

        for (TweetMarkersRequestByNameTo markerRequest : request) {
            if (markerRequest == null || markerRequest.getMarkerName() == null) {
                continue;
            }

            String name = markerRequest.getMarkerName().trim();
            if (name.isEmpty()) {
                continue;
            }

            Marker marker = markerRepository.findMarkerByName(name)
                    .orElseGet(() -> markerRepository.save(Marker.builder().name(name).build()));

            response.add(MarkerResponseByNameTo.builder()
                    .id(marker.getId())
                    .build());
        }

        return response;
    }

    private Marker toEntity(MarkerRequestTo request) {
        return Marker.builder()
                .name(request.getName())
                .build();
    }

    private MarkerResponseTo toResponse(Marker entity) {
        return MarkerResponseTo.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
