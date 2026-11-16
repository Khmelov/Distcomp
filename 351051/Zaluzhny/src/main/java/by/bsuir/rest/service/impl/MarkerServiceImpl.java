package by.bsuir.rest.service.impl;

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

@Service
@Transactional
@RequiredArgsConstructor
public class MarkerServiceImpl implements MarkerService {

    private final MarkerRepository repository;
    private final MarkerMapper mapper;
    private final RequestValidator requestValidator;
    private final UpdateIdResolver updateIdResolver;

    @Override
    public MarkerResponseTo create(MarkerRequestTo request) {
        requestValidator.validate(request);
        ensureNameAvailable(request.getName(), null);
        Marker marker = mapper.toEntity(request);
        marker.setId(null);
        return mapper.toResponse(repository.save(marker));
    }

    @Override
    public MarkerResponseTo getById(Long id) {
        return mapper.toResponse(find(id));
    }

    @Override
    public List<MarkerResponseTo> getAll() {
        return repository.find(MarkerFilter.empty(), PageQuery.all()).content().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public MarkerResponseTo update(Long pathId, MarkerRequestTo request) {
        requestValidator.validate(request);
        Long id = updateIdResolver.resolve(pathId, request.getId());
        Marker marker = find(id);
        ensureNameAvailable(request.getName(), id);
        mapper.update(request, marker);
        return mapper.toResponse(marker);
    }

    @Override
    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw ApiException.notFound("Marker", id);
        }
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
