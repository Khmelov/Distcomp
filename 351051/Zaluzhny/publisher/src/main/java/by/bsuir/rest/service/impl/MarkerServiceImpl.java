package by.bsuir.rest.service.impl;

import by.bsuir.rest.cache.ResponseCache;
import by.bsuir.rest.dto.request.MarkerRequestTo;
import by.bsuir.rest.dto.response.MarkerResponseTo;
import by.bsuir.rest.entity.Marker;
import by.bsuir.rest.exception.ApiException;
import by.bsuir.rest.exception.ErrorCode;
import by.bsuir.rest.mapper.MarkerMapper;
import by.bsuir.rest.repository.MarkerFilter;
import by.bsuir.rest.repository.MarkerRepository;
import by.bsuir.rest.repository.PageQuery;
import by.bsuir.rest.service.MarkerService;
import by.bsuir.rest.service.RequestValidator;
import by.bsuir.rest.service.UpdateIdResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MarkerServiceImpl implements MarkerService {

    private final MarkerRepository repository;
    private final MarkerMapper mapper;
    private final RequestValidator requestValidator;
    private final UpdateIdResolver updateIdResolver;
    private final ResponseCache cache;

    @Override
    public MarkerResponseTo create(MarkerRequestTo request) {
        requestValidator.validate(request);
        ensureNameAvailable(request.getName(), null);
        Marker marker = mapper.toEntity(request);
        marker.setId(null);
        MarkerResponseTo response = mapper.toResponse(repository.save(marker));
        cache.put(ResponseCache.MARKER, response.getId(), response);
        cache.evict(ResponseCache.MARKER, ResponseCache.ALL);
        return response;
    }

    @Override
    public MarkerResponseTo getById(Long id) {
        return cache.get(ResponseCache.MARKER, id, MarkerResponseTo.class)
                .orElseGet(() -> {
                    MarkerResponseTo response = mapper.toResponse(find(id));
                    cache.put(ResponseCache.MARKER, id, response);
                    return response;
                });
    }

    @Override
    public List<MarkerResponseTo> getAll() {
        Optional<List<MarkerResponseTo>> cached = cache.getList(ResponseCache.MARKER, ResponseCache.ALL, MarkerResponseTo.class);
        if (cached.isPresent()) {
            return cached.get();
        }
        List<MarkerResponseTo> responses = repository.find(MarkerFilter.empty(), PageQuery.all()).content().stream()
                .map(mapper::toResponse)
                .toList();
        cache.put(ResponseCache.MARKER, ResponseCache.ALL, responses);
        return responses;
    }

    @Override
    public MarkerResponseTo update(Long pathId, MarkerRequestTo request) {
        requestValidator.validate(request);
        Long id = updateIdResolver.resolve(pathId, request.getId());
        Marker marker = find(id);
        ensureNameAvailable(request.getName(), id);
        mapper.update(request, marker);
        MarkerResponseTo response = mapper.toResponse(marker);
        cache.put(ResponseCache.MARKER, response.getId(), response);
        cache.evict(ResponseCache.MARKER, ResponseCache.ALL);
        return response;
    }

    @Override
    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw ApiException.notFound("Marker", id);
        }
        cache.evict(ResponseCache.MARKER, id);
        cache.evict(ResponseCache.MARKER, ResponseCache.ALL);
    }

    private Marker find(Long id) {
        return repository.findById(id).orElseThrow(() -> ApiException.notFound("Marker", id));
    }

    private void ensureNameAvailable(String name, Long currentId) {
        boolean taken = repository.find(new MarkerFilter(name), PageQuery.all()).content().stream()
                .anyMatch(marker -> name.equals(marker.getName()) && !marker.getId().equals(currentId));
        if (taken) {
            throw new ApiException(HttpStatus.FORBIDDEN, ErrorCode.DUPLICATE, "Marker name must be unique");
        }
    }
}
