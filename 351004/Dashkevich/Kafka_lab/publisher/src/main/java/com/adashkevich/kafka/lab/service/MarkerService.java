package com.adashkevich.kafka.lab.service;

import com.adashkevich.kafka.lab.dto.request.MarkerRequestTo;
import com.adashkevich.kafka.lab.dto.response.MarkerResponseTo;
import com.adashkevich.kafka.lab.exception.ConflictException;
import com.adashkevich.kafka.lab.exception.NotFoundException;
import com.adashkevich.kafka.lab.model.News;
import com.adashkevich.kafka.lab.repository.MarkerRepository;
import com.adashkevich.kafka.lab.repository.NewsRepository;
import com.adashkevich.kafka.lab.mapper.MarkerMapper;
import com.adashkevich.kafka.lab.model.Marker;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MarkerService {
    private final MarkerRepository repo;
    private final NewsRepository newsRepo;
    private final MarkerMapper mapper;

    public MarkerService(MarkerRepository repo, NewsRepository newsRepo, MarkerMapper mapper) {
        this.repo = repo;
        this.newsRepo = newsRepo;
        this.mapper = mapper;
    }

    @Transactional
    public MarkerResponseTo create(MarkerRequestTo dto) {
        ensureNameUnique(dto.name, null);
        Marker saved = repo.save(mapper.toEntity(dto));
        return mapper.toResponse(saved);
    }

    public List<MarkerResponseTo> getAll() {
        return repo.findAll(Sort.by("id")).stream().map(mapper::toResponse).toList();
    }

    public MarkerResponseTo getById(Long id) {
        return mapper.toResponse(findExisting(id));
    }

    @Transactional
    public MarkerResponseTo update(Long id, MarkerRequestTo dto) {
        Marker existing = findExisting(id);
        ensureNameUnique(dto.name, id);
        existing.setName(dto.name);
        return mapper.toResponse(repo.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        Marker marker = findExisting(id);
        for (News news : newsRepo.findAll()) news.getMarkers().removeIf(m -> id.equals(m.getId()));
        repo.delete(marker);
    }

    private Marker findExisting(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Marker not found", "40430"));
    }

    private void ensureNameUnique(String name, Long selfId) {
        boolean exists = selfId == null ? repo.existsByNameIgnoreCase(name) : repo.existsByNameIgnoreCaseAndIdNot(name, selfId);
        if (exists) throw new ConflictException("Marker name must be unique", "40930");
    }
}
