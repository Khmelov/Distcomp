package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.request.MarkerRequestTo;
import by.bsuir.distcomp.dto.response.MarkerResponseTo;
import by.bsuir.distcomp.entity.Marker;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.mapper.MarkerMapper;
import by.bsuir.distcomp.repository.impl.InMemoryMarkerRepository;
import by.bsuir.distcomp.repository.impl.InMemoryTweetRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MarkerService {
    private final InMemoryMarkerRepository markerRepository;
    private final InMemoryTweetRepository tweetRepository;
    private final MarkerMapper markerMapper;

    public MarkerService(InMemoryMarkerRepository markerRepository,
                        InMemoryTweetRepository tweetRepository,
                        MarkerMapper markerMapper) {
        this.markerRepository = markerRepository;
        this.tweetRepository = tweetRepository;
        this.markerMapper = markerMapper;
    }

    public MarkerResponseTo create(MarkerRequestTo dto) {
        Marker entity = markerMapper.toEntity(dto);
        Marker saved = markerRepository.save(entity);
        return markerMapper.toResponseDto(saved);
    }

    public MarkerResponseTo getById(Long id) {
        Marker entity = markerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marker with id " + id + " not found", 40412));
        return markerMapper.toResponseDto(entity);
    }

    public List<MarkerResponseTo> getAll() {
        return markerRepository.findAll().stream()
                .map(markerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public MarkerResponseTo update(MarkerRequestTo dto) {
        Marker existingMarker = markerRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Marker with id " + dto.getId() + " not found", 40413));
        
        Marker entity = markerMapper.toEntity(dto);
        Marker updated = markerRepository.update(entity);
        return markerMapper.toResponseDto(updated);
    }

    public void deleteById(Long id) {
        if (!markerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Marker with id " + id + " not found", 40414);
        }
        markerRepository.deleteById(id);
    }

    public List<MarkerResponseTo> getMarkersByTweetId(Long tweetId) {
        if (!tweetRepository.existsById(tweetId)) {
            throw new ResourceNotFoundException("Tweet with id " + tweetId + " not found", 40415);
        }
        
        Map<Long, Set<Long>> tweetToMarkers = new HashMap<>();
        tweetRepository.findAll().forEach(tweet -> {
            if (tweet.getMarkerIds() != null) {
                tweetToMarkers.put(tweet.getId(), tweet.getMarkerIds());
            }
        });
        
        return markerRepository.findByTweetId(tweetId, tweetToMarkers).stream()
                .map(markerMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
