package by.bsuir.rest.service.impl;

import by.bsuir.rest.cache.ResponseCache;
import by.bsuir.rest.dto.request.MarkerRequestTo;
import by.bsuir.rest.dto.request.StoryRequestTo;
import by.bsuir.rest.dto.response.MarkerResponseTo;
import by.bsuir.rest.dto.response.StoryResponseTo;
import by.bsuir.rest.dto.response.UserResponseTo;
import by.bsuir.rest.entity.Marker;
import by.bsuir.rest.entity.Story;
import by.bsuir.rest.exception.ApiException;
import by.bsuir.rest.exception.ErrorCode;
import by.bsuir.rest.mapper.MarkerMapper;
import by.bsuir.rest.mapper.StoryMapper;
import by.bsuir.rest.repository.MarkerFilter;
import by.bsuir.rest.repository.MarkerRepository;
import by.bsuir.rest.repository.PageQuery;
import by.bsuir.rest.repository.StoryRepository;
import by.bsuir.rest.repository.UserRepository;
import by.bsuir.rest.service.RequestValidator;
import by.bsuir.rest.service.StorySearchCriteria;
import by.bsuir.rest.service.StoryService;
import by.bsuir.rest.service.UpdateIdResolver;
import by.bsuir.rest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryRepository repository;
    private final UserRepository userRepository;
    private final MarkerRepository markerRepository;
    private final StoryMapper mapper;
    private final MarkerMapper markerMapper;
    private final RequestValidator requestValidator;
    private final UpdateIdResolver updateIdResolver;
    private final UserService userService;
    private final ResponseCache cache;

    @Override
    public StoryResponseTo create(StoryRequestTo request) {
        requestValidator.validate(request);
        ensureUserExists(request.getUserId());
        ensureTitleAvailable(request.getTitle(), null);
        Story story = mapper.toEntity(request);
        story.setId(null);
        Instant now = timestamp();
        story.setCreated(now);
        story.setModified(now);
        applyMarkers(story, request);
        StoryResponseTo response = toResponse(repository.save(story));
        rememberStory(response);
        cache.evict(ResponseCache.MARKER, ResponseCache.ALL);
        return response;
    }

    @Override
    public StoryResponseTo getById(Long id) {
        return cache.get(ResponseCache.STORY, id, StoryResponseTo.class)
                .orElseGet(() -> {
                    StoryResponseTo response = toResponse(find(id));
                    cache.put(ResponseCache.STORY, id, response);
                    return response;
                });
    }

    @Override
    public List<StoryResponseTo> find(StorySearchCriteria criteria) {
        if (criteria.unfiltered()) {
            Optional<List<StoryResponseTo>> cached = cache.getList(
                    ResponseCache.STORY, ResponseCache.ALL, StoryResponseTo.class);
            if (cached.isPresent()) {
                return cached.get();
            }
        }
        List<StoryResponseTo> responses = repository.find(criteria, PageQuery.all()).content().stream()
                .map(this::toResponse)
                .toList();
        if (criteria.unfiltered()) {
            cache.put(ResponseCache.STORY, ResponseCache.ALL, responses);
        }
        return responses;
    }

    @Override
    public StoryResponseTo update(Long pathId, StoryRequestTo request) {
        requestValidator.validate(request);
        Long id = updateIdResolver.resolve(pathId, request.getId());
        Story story = find(id);
        ensureUserExists(request.getUserId());
        ensureTitleAvailable(request.getTitle(), id);
        mapper.update(request, story);
        if (request.getMarkerIds() != null || request.getMarkers() != null) {
            applyMarkers(story, request);
        }
        story.setModified(nextModified(story.getCreated()));
        StoryResponseTo response = toResponse(story);
        rememberStory(response);
        cache.evict(ResponseCache.MARKER, ResponseCache.ALL);
        return response;
    }

    @Override
    public void delete(Long id) {
        Story story = repository.findById(id).orElseThrow(() -> ApiException.notFound("Story", id));
        Set<Long> markerIds = new LinkedHashSet<>(story.getMarkerIds());
        if (!repository.deleteById(id)) {
            throw ApiException.notFound("Story", id);
        }
        List<Long> removedMarkers = new ArrayList<>();
        for (Long markerId : markerIds) {
            StorySearchCriteria criteria = new StorySearchCriteria(List.of(), List.of(markerId), null, null, null);
            if (repository.find(criteria, PageQuery.all()).content().isEmpty()) {
                markerRepository.deleteById(markerId);
                removedMarkers.add(markerId);
            }
        }
        cache.evict(ResponseCache.STORY, id);
        cache.evict(ResponseCache.STORY, ResponseCache.ALL);
        for (Long markerId : removedMarkers) {
            cache.evict(ResponseCache.MARKER, markerId);
        }
        if (!removedMarkers.isEmpty()) {
            cache.evict(ResponseCache.MARKER, ResponseCache.ALL);
        }
    }

    @Override
    public UserResponseTo getAuthor(Long storyId) {
        Story story = find(storyId);
        return userService.getById(story.getUserId());
    }

    @Override
    public List<MarkerResponseTo> getMarkers(Long storyId) {
        Story story = find(storyId);
        if (story.getMarkerIds().isEmpty()) {
            return List.of();
        }
        return story.getMarkerIds().stream()
                .sorted()
                .map(markerRepository::findById)
                .flatMap(Optional::stream)
                .map(markerMapper::toResponse)
                .toList();
    }

    private void rememberStory(StoryResponseTo response) {
        cache.put(ResponseCache.STORY, response.getId(), response);
        cache.evict(ResponseCache.STORY, ResponseCache.ALL);
    }

    private Story find(Long id) {
        return repository.findById(id).orElseThrow(() -> ApiException.notFound("Story", id));
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.RELATED_NOT_FOUND,
                    "User with id " + userId + " not found");
        }
    }

    private void ensureTitleAvailable(String title, Long currentId) {
        StorySearchCriteria criteria = new StorySearchCriteria(List.of(), List.of(), null, title, null);
        boolean taken = repository.find(criteria, PageQuery.all()).content().stream()
                .anyMatch(story -> title.equals(story.getTitle()) && !story.getId().equals(currentId));
        if (taken) {
            throw new ApiException(HttpStatus.FORBIDDEN, ErrorCode.DUPLICATE, "Story title must be unique");
        }
    }

    private void applyMarkers(Story story, StoryRequestTo request) {
        if (request.getMarkerIds() == null && request.getMarkers() == null) {
            return;
        }
        Set<Long> uniqueIds = new LinkedHashSet<>();
        if (request.getMarkerIds() != null) {
            for (Long markerId : request.getMarkerIds()) {
                if (markerId == null || !markerRepository.existsById(markerId)) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.RELATED_NOT_FOUND,
                            "Marker with id " + markerId + " not found");
                }
                uniqueIds.add(markerId);
            }
        }
        if (request.getMarkers() != null) {
            for (MarkerRequestTo marker : request.getMarkers()) {
                uniqueIds.add(resolveMarker(marker));
            }
        }
        story.setMarkerIds(uniqueIds);
    }

    private Long resolveMarker(MarkerRequestTo request) {
        if (request != null && request.getName() != null && !request.getName().isBlank()) {
            requestValidator.validate(request);
            return markerRepository.find(new MarkerFilter(request.getName()), PageQuery.all()).content().stream()
                    .filter(marker -> request.getName().equals(marker.getName()))
                    .map(Marker::getId)
                    .findFirst()
                    .orElseGet(() -> {
                        Marker marker = new Marker();
                        marker.setName(request.getName());
                        return markerRepository.save(marker).getId();
                    });
        }
        if (request != null && request.getId() != null && markerRepository.existsById(request.getId())) {
            return request.getId();
        }
        Long markerId = request == null ? null : request.getId();
        throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.RELATED_NOT_FOUND,
                "Marker with id " + markerId + " not found");
    }

    private StoryResponseTo toResponse(Story story) {
        StoryResponseTo response = mapper.toResponse(story);
        if (story.getMarkerIds().isEmpty()) {
            response.setMarkerIds(null);
        } else {
            response.setMarkerIds(story.getMarkerIds().stream().sorted().toList());
        }
        return response;
    }

    private Instant nextModified(Instant created) {
        Instant now = timestamp();
        if (created != null && !now.isAfter(created)) {
            return created.plus(1, ChronoUnit.MICROS);
        }
        return now;
    }

    private static Instant timestamp() {
        return Instant.now().truncatedTo(ChronoUnit.MICROS);
    }
}
