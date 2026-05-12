package com.adashkevich.redis.lab.service;

import com.adashkevich.redis.lab.dto.request.MarkerRequestTo;
import com.adashkevich.redis.lab.dto.response.MarkerResponseTo;
import com.adashkevich.redis.lab.exception.ConflictException;
import com.adashkevich.redis.lab.exception.NotFoundException;
import com.adashkevich.redis.lab.model.News;
import com.adashkevich.redis.lab.repository.MarkerRepository;
import com.adashkevich.redis.lab.repository.NewsRepository;
import com.adashkevich.redis.lab.mapper.MarkerMapper;
import com.adashkevich.redis.lab.model.Marker;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @CacheEvict(cacheNames = {"markers"}, allEntries = true)
    @Transactional
    public MarkerResponseTo create(MarkerRequestTo dto) {
        ensureNameUnique(dto.name, null);
        Marker saved = repo.save(mapper.toEntity(dto));
        return mapper.toResponse(saved);
    }

    @Cacheable(cacheNames = "markers")
    public List<MarkerResponseTo> getAll() {
        return repo.findAll(Sort.by("id")).stream().map(mapper::toResponse).toList();
    }

    @Cacheable(cacheNames = "marker", key = "#id")
    public MarkerResponseTo getById(Long id) {
        return mapper.toResponse(findExisting(id));
    }

    @CacheEvict(cacheNames = {"markers", "marker", "news", "newsItem"}, allEntries = true)
    @Transactional
    public MarkerResponseTo update(Long id, MarkerRequestTo dto) {
        Marker existing = findExisting(id);
        ensureNameUnique(dto.name, id);
        existing.setName(dto.name);
        return mapper.toResponse(repo.save(existing));
    }

    @CacheEvict(cacheNames = {"markers", "marker", "news", "newsItem"}, allEntries = true)
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
