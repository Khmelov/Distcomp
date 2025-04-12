package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.mapper.MarkerMapper;
import by.bsuir.distcomp.dto.request.MarkerRequestTo;
import by.bsuir.distcomp.dto.response.MarkerResponseTo;
import by.bsuir.distcomp.repository.MarkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MarkerService {

    private final MarkerRepository markerRepository;
    private final MarkerMapper markerMapper;

    public MarkerService(MarkerRepository markerRepository, MarkerMapper markerMapper) {
        this.markerRepository = markerRepository;
        this.markerMapper = markerMapper;
    }

    public List<MarkerResponseTo> getAllMarkers() {
        return markerRepository.findAll().stream().map(markerMapper::toDto).toList();
    }

    public MarkerResponseTo getMarkerById(Long id) {
        return markerRepository.findById(id)
                .map(markerMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Marker with id: " + id + " not found"));
    }

    @Transactional
    public MarkerResponseTo createMarker(MarkerRequestTo markerRequestTo) {
        return markerMapper.toDto(markerRepository.save(markerMapper.toEntity(markerRequestTo)));
    }

    @Transactional
    public MarkerResponseTo updateMarker(MarkerRequestTo markerRequestTo) {
        getMarkerById(markerRequestTo.getId());
        return markerMapper.toDto(markerRepository.save(markerMapper.toEntity(markerRequestTo)));
    }

    @Transactional
    public void deleteMarker(Long id) {
        getMarkerById(id);
        markerRepository.deleteById(id);
    }
    
}
