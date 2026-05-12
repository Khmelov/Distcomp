package com.adashkevich.rest.lab.service;

import com.adashkevich.rest.lab.dto.request.MarkerRequestTo;
import com.adashkevich.rest.lab.dto.response.MarkerResponseTo;
import com.adashkevich.rest.lab.exception.ConflictException;
import com.adashkevich.rest.lab.exception.NotFoundException;
import com.adashkevich.rest.lab.mapper.MarkerMapper;
import com.adashkevich.rest.lab.model.Marker;
import com.adashkevich.rest.lab.model.News;
import com.adashkevich.rest.lab.repository.MarkerRepository;
import com.adashkevich.rest.lab.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MarkerService {
    private final MarkerRepository repo;
    private final NewsRepository newsRepo;
    private final MarkerMapper mapper;

    public MarkerService(MarkerRepository repo, NewsRepository newsRepo, MarkerMapper mapper) {
        this.repo = repo;
        this.newsRepo = newsRepo;
        this.mapper = mapper;
    }

    public MarkerResponseTo create(MarkerRequestTo dto) {
        ensureNameUnique(dto.name, null);

        Marker entity = mapper.toEntity(dto);
        Marker saved = repo.save(entity);
        return mapper.toResponse(saved);
    }

    public List<MarkerResponseTo> getAll() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    public MarkerResponseTo getById(Long id) {
        Marker m = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Marker not found", "40430"));
        return mapper.toResponse(m);
    }

    public MarkerResponseTo update(Long id, MarkerRequestTo dto) {
        Marker existing = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Marker not found", "40430"));

        ensureNameUnique(dto.name, id);

        existing.setName(dto.name);
        Marker updated = repo.update(id, existing);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        Marker marker = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Marker not found", "40430"));

        for (News news : newsRepo.findAll()) {
            Set<Long> markerIds = news.getMarkerIds();
            if (markerIds != null && markerIds.contains(id)) {
                Set<Long> updatedIds = new HashSet<>(markerIds);
                updatedIds.remove(id);
                news.setMarkerIds(updatedIds);
                newsRepo.update(news.getId(), news);
            }
        }

        repo.deleteById(marker.getId());
    }

    private void ensureNameUnique(String name, Long selfId) {
        boolean exists = repo.findAll().stream()
                .anyMatch(marker ->
                        marker.getName().equalsIgnoreCase(name)
                                && (selfId == null || !marker.getId().equals(selfId)));

        if (exists) {
            throw new ConflictException("Marker name must be unique", "40930");
        }
    }
}