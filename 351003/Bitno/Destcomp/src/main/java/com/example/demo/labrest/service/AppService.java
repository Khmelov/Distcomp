package com.example.demo.labrest.service;

import com.example.demo.labrest.dto.*;
import com.example.demo.labrest.exception.ForbiddenException;
import com.example.demo.labrest.mapper.AppMapper;
import com.example.demo.labrest.model.Creator;
import com.example.demo.labrest.model.Marker;
import com.example.demo.labrest.model.Notice;
import com.example.demo.labrest.model.Topic;
import com.example.demo.labrest.repository.CreatorRepository;
import com.example.demo.labrest.repository.MarkerRepository;
import com.example.demo.labrest.repository.TopicRepository;
import com.example.demo.labrest.exception.NotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class AppService {

    private final CreatorRepository creatorRepo;
    private final TopicRepository topicRepo;
    private final MarkerRepository markerRepo;
    private final AppMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final NoticeKafkaBridge kafkaBridge;

    @Transactional(readOnly = true)
    public CreatorResponseTo getCreatorByLogin(String login) {
        return creatorRepo.findByLogin(login)
                .map(mapper::toCreatorResponse)
                .orElseThrow(() -> new NotFoundException("Creator with login " + login, null));
    }

    public AppService(CreatorRepository creatorRepo,
                      TopicRepository topicRepo,
                      MarkerRepository markerRepo,
                      AppMapper mapper,
                      PasswordEncoder passwordEncoder,
                      NoticeKafkaBridge kafkaBridge) {
        this.creatorRepo = creatorRepo;
        this.topicRepo = topicRepo;
        this.markerRepo = markerRepo;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.kafkaBridge = kafkaBridge;
    }

    @CacheEvict(value = {"creators", "creatorById"}, allEntries = true)
    public CreatorResponseTo createCreator(CreatorRequestTo req) {
        if (creatorRepo.existsByLogin(req.getLogin())) {
            throw new ForbiddenException("Creator with login " + req.getLogin() + " already exists");
        }
        Creator creator = mapper.toCreator(req);
        creator.setPassword(passwordEncoder.encode(req.getPassword()));
        creator.setRole(req.getRole() != null ? req.getRole().toUpperCase() : "CUSTOMER");
        return mapper.toCreatorResponse(creatorRepo.save(creator));
    }

    @Cacheable(value = "creatorById", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public CreatorResponseTo getCreator(Long id) {
        return creatorRepo.findById(id)
                .map(mapper::toCreatorResponse)
                .orElseThrow(() -> new NotFoundException("Creator", id));
    }

    @Transactional(readOnly = true)
    public List<CreatorResponseTo> getAllCreators() {
        return creatorRepo.findAll().stream()
                .map(mapper::toCreatorResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CreatorResponseTo> getAllCreators(Pageable pageable) {
        return creatorRepo.findAll().stream()
                .map(mapper::toCreatorResponse)
                .collect(Collectors.toList());
    }

    @CachePut(value = "creatorById", key = "#id")
    @CacheEvict(value = {"creators"}, allEntries = true)
    public CreatorResponseTo updateCreator(Long id, CreatorRequestTo req) {
        Creator existing = creatorRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Creator", id));

        existing.setLogin(req.getLogin());
        existing.setFirstname(req.getFirstname());
        existing.setLastname(req.getLastname());
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getRole() != null) {
            existing.setRole(req.getRole().toUpperCase());
        }
        return mapper.toCreatorResponse(creatorRepo.save(existing));
    }

    @CacheEvict(value = {"creators", "creatorById"}, allEntries = true)
    @Transactional
    public void deleteCreator(Long id) {
        creatorRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Creator", id));
        creatorRepo.deleteById(id);
    }

    public TopicResponseTo createTopic(TopicRequestTo req) {
        Creator creator = creatorRepo.findById(req.getCreatorId())
                .orElseThrow(() -> new ForbiddenException("Creator with id " + req.getCreatorId() + " does not exist"));

        if (topicRepo.existsByCreatorAndTitle(creator, req.getTitle())) {
            throw new ForbiddenException("Topic with title '" + req.getTitle() + "' already exists for this creator");
        }

        Topic topic = mapper.toTopic(req);
        topic.setCreator(creator);
        topic.setCreated(LocalDateTime.now());
        topic.setModified(LocalDateTime.now());

        if (req.getMarkerIds() != null && !req.getMarkerIds().isEmpty()) {
            Set<Marker> markers = req.getMarkerIds().stream()
                    .map(mid -> markerRepo.findById(mid)
                            .orElseThrow(() -> new NotFoundException("Marker", mid)))
                    .collect(Collectors.toSet());
            topic.setMarkers(markers);
        }
        if (req.getMarkers() != null && !req.getMarkers().isEmpty()) {
            Set<Marker> markers = req.getMarkers().stream()
                    .map(markerName -> markerRepo.findByName(markerName)
                            .orElseGet(() -> {
                                Marker newMarker = new Marker();
                                newMarker.setName(markerName);
                                return markerRepo.save(newMarker);
                            }))
                    .collect(Collectors.toSet());
            topic.setMarkers(markers);
        }

        Topic saved = topicRepo.save(topic);
        return mapper.toTopicResponseWithRelations(saved);
    }

    @Cacheable(value = "topicById", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public TopicResponseTo getTopic(Long id) {
        return topicRepo.findById(id)
                .map(mapper::toTopicResponseWithRelations)
                .orElseThrow(() -> new NotFoundException("Topic", id));
    }

    @Transactional(readOnly = true)
    public List<TopicResponseTo> getAllTopics() {
        return topicRepo.findAll().stream()
                .map(mapper::toTopicResponseWithRelations)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopicResponseTo> getAllTopics(Pageable pageable) {
        return topicRepo.findAll().stream()
                .map(mapper::toTopicResponseWithRelations)
                .collect(Collectors.toList());
    }

    @CachePut(value = "topicById", key = "#id")
    @CacheEvict(value = {"topics", "topicsByCreator"}, allEntries = true)
    public TopicResponseTo updateTopic(Long id, TopicRequestTo req) {
        return updateTopicInternal(id, req, null);
    }

    @CachePut(value = "topicById", key = "#id")
    @CacheEvict(value = {"topics", "topicsByCreator"}, allEntries = true)
    public TopicResponseTo updateTopicSecured(Long id, TopicRequestTo req, Authentication auth) {
        checkOwnerOrAdmin(req.getCreatorId(), auth);
        return updateTopicInternal(id, req, auth);
    }

    private TopicResponseTo updateTopicInternal(Long id, TopicRequestTo req, Authentication auth) {
        Topic topic = topicRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Topic", id));

        Creator creator = creatorRepo.findById(req.getCreatorId())
                .orElseThrow(() -> new ForbiddenException("Creator with id " + req.getCreatorId() + " does not exist"));

        if (topicRepo.existsByCreatorAndTitle(creator, req.getTitle())) {
            throw new ForbiddenException("Topic with title '" + req.getTitle() + "' already exists for this creator");
        }

        topic.setCreator(creator);
        topic.setTitle(req.getTitle());
        topic.setContent(req.getContent());
        topic.setModified(LocalDateTime.now());

        if (req.getMarkerIds() != null) {
            Set<Marker> markers = req.getMarkerIds().stream()
                    .map(mid -> markerRepo.findById(mid)
                            .orElseThrow(() -> new NotFoundException("Marker", mid)))
                    .collect(Collectors.toSet());
            topic.setMarkers(markers);
        }

        Topic updated = topicRepo.save(topic);
        return mapper.toTopicResponseWithRelations(updated);
    }

    @CacheEvict(value = {"topics", "topicById", "topicsByCreator"}, allEntries = true)
    @Transactional
    public void deleteTopicAndOrphanMarkers(Long topicId) {
        Topic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Topic", topicId));

        Set<Marker> markersToCheck = new HashSet<>(topic.getMarkers());
        topicRepo.delete(topic);
        topicRepo.flush();

        for (Marker marker : markersToCheck) {
            long count = topicRepo.countByMarkersContains(marker);
            if (count == 0) {
                markerRepo.delete(marker);
            }
        }
    }

    @CacheEvict(value = {"topics", "topicById"}, allEntries = true)
    @Transactional
    public void deleteTopic(Long id) {
        topicRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Topic", id));
        topicRepo.deleteById(id);
    }

    @CacheEvict(value = {"markers"}, allEntries = true)
    public MarkerResponseTo createMarker(MarkerRequestTo req) {
        return mapper.toMarkerResponse(markerRepo.save(mapper.toMarker(req)));
    }

    @Cacheable(value = "markerById", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public MarkerResponseTo getMarker(Long id) {
        return markerRepo.findById(id)
                .map(mapper::toMarkerResponse)
                .orElseThrow(() -> new NotFoundException("Marker", id));
    }

    @Transactional(readOnly = true)
    public List<MarkerResponseTo> getAllMarkers() {
        return markerRepo.findAll().stream()
                .map(mapper::toMarkerResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MarkerResponseTo> getAllMarkers(Pageable pageable) {
        return markerRepo.findAll().stream()
                .map(mapper::toMarkerResponse)
                .collect(Collectors.toList());
    }

    @CachePut(value = "markerById", key = "#id")
    @CacheEvict(value = {"markers"}, allEntries = true)
    public MarkerResponseTo updateMarker(Long id, MarkerRequestTo req) {
        Marker m = markerRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Marker", id));
        m.setName(req.getName());
        return mapper.toMarkerResponse(markerRepo.save(m));
    }

    @CacheEvict(value = {"markers", "markerById"}, allEntries = true)
    @Transactional
    public void deleteMarker(Long id) {
        markerRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Marker", id));
        markerRepo.deleteById(id);
    }

    public NoticeResponseTo createNotice(NoticeRequestTo req) {
        topicRepo.findById(req.getTopicId())
                .orElseThrow(() -> new NotFoundException("Topic", req.getTopicId()));

        Long noticeId = System.nanoTime();

        var kafkaRequest = KafkaNoticeRequest.builder()
                .operation(KafkaNoticeRequest.Operation.CREATE)
                .id(noticeId)
                .topicId(req.getTopicId())
                .content(req.getContent())
                .country(null)
                .build();

        var future = kafkaBridge.sendRequest(kafkaRequest);
        KafkaNoticeResponse response;
        try {
            response = future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Timeout creating notice", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while creating notice", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create notice", e);
        }

        if (response == null) {
            throw new RuntimeException("Empty response from discussion module");
        }
        if (response.getState() != KafkaNoticeResponse.State.SUCCESS) {
            throw new RuntimeException("Notice creation failed: " + response.getReason());
        }

        Notice temp = Notice.builder()
                .id(noticeId)
                .topicId(req.getTopicId())
                .content(req.getContent())
                .build();
        return mapper.toNoticeResponse(temp);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "noticeById", key = "#id", unless = "#result == null")
    public NoticeResponseTo getNotice(Long id) {
        var request = KafkaNoticeRequest.builder()
                .operation(KafkaNoticeRequest.Operation.READ)
                .id(id)
                .build();

        var future = kafkaBridge.sendRequest(request);

        try {
            KafkaNoticeResponse response = future.get(10, TimeUnit.SECONDS);

            if (response == null) {
                throw new RuntimeException("Empty response from discussion module");
            }
            if (response.getState() == KafkaNoticeResponse.State.NOT_FOUND) {
                throw new NotFoundException("Notice", id);
            }
            if (response.getState() != KafkaNoticeResponse.State.SUCCESS) {
                throw new RuntimeException("Failed to get notice: " + response.getReason());
            }
            if (response.getId() == null || response.getTopicId() == null) {
                throw new RuntimeException("Invalid response: missing id or topicId");
            }

            Notice temp = Notice.builder()
                    .id(response.getId())
                    .topicId(response.getTopicId())
                    .content(response.getContent())
                    .build();
            return mapper.toNoticeResponse(temp);

        } catch (TimeoutException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Timeout getting notice", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted getting notice", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get notice", e);
        }
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseTo> getAllNotices() {
        var request = KafkaNoticeRequest.builder()
                .operation(KafkaNoticeRequest.Operation.READ)
                .build();

        var future = kafkaBridge.sendRequest(request);
        try {
            KafkaNoticeResponse response = future.get(10, TimeUnit.SECONDS);
            if (response == null || response.getState() != KafkaNoticeResponse.State.SUCCESS) {
                return List.of();
            }
            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseTo> getAllNotices(Pageable pageable) {
        return getAllNotices();
    }

    @CacheEvict(value = "noticeById", key = "#id")
    public NoticeResponseTo updateNotice(Long id, NoticeRequestTo req) {
        var request = KafkaNoticeRequest.builder()
                .operation(KafkaNoticeRequest.Operation.UPDATE)
                .id(id)
                .topicId(req.getTopicId())
                .content(req.getContent())
                .build();

        var future = kafkaBridge.sendRequest(request);
        try {
            KafkaNoticeResponse response = future.get(10, TimeUnit.SECONDS);

            if (response == null) {
                throw new RuntimeException("Empty response from discussion module");
            }
            if (response.getState() == KafkaNoticeResponse.State.NOT_FOUND) {
                throw new NotFoundException("Notice", id);
            }
            if (response.getState() != KafkaNoticeResponse.State.SUCCESS) {
                throw new RuntimeException("Failed to update notice: " + response.getReason());
            }
            if (response.getId() == null) {
                throw new RuntimeException("Invalid response: missing id");
            }

            Notice temp = Notice.builder()
                    .id(response.getId())
                    .topicId(req.getTopicId())
                    .content(req.getContent())
                    .build();
            return mapper.toNoticeResponse(temp);

        } catch (TimeoutException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Timeout updating notice", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while updating notice", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update notice", e);
        }
    }

    @Transactional
    @CacheEvict(value = "noticeById", key = "#id")
    public void deleteNotice(Long id) {
        var request = KafkaNoticeRequest.builder()
                .operation(KafkaNoticeRequest.Operation.DELETE)
                .id(id)
                .build();

        var future = kafkaBridge.sendRequest(request);
        KafkaNoticeResponse response;
        try {
            response = future.get(10, TimeUnit.SECONDS);

            if (response == null) {
                throw new RuntimeException("Empty response from discussion module");
            }
            if (response.getState() == KafkaNoticeResponse.State.NOT_FOUND) {
                throw new NotFoundException("Notice", id);
            }
            if (response.getState() != KafkaNoticeResponse.State.SUCCESS) {
                throw new RuntimeException("Failed to delete notice: " + response.getReason());
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (TimeoutException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Timeout deleting notice", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while deleting notice", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete notice", e);
        }
    }

    @Transactional(readOnly = true)
    public CreatorResponseTo getCreatorByTopicId(Long topicId) {
        Topic t = topicRepo.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Topic", topicId));
        return mapper.toCreatorResponse(t.getCreator());
    }

    @Cacheable(value = "markersByTopic", key = "#topicId")
    @Transactional(readOnly = true)
    public List<MarkerResponseTo> getMarkersByTopicId(Long topicId) {
        Topic t = topicRepo.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Topic", topicId));
        return t.getMarkers().stream()
                .map(mapper::toMarkerResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<NoticeResponseTo> getNoticesByTopicId(Long topicId) {
        var request = KafkaNoticeRequest.builder()
                .operation(KafkaNoticeRequest.Operation.READ_ALL_BY_TOPIC)
                .topicId(topicId)
                .build();

        var future = kafkaBridge.sendRequest(request);

        try {
            KafkaNoticeResponse response = future.get(10, TimeUnit.SECONDS);

            if (response == null || response.getState() != KafkaNoticeResponse.State.SUCCESS) {
                return List.of();
            }

            if (response.getNotices() == null) {
                return List.of();
            }

            return response.getNotices().stream()
                    .map(data -> NoticeResponseTo.builder()
                            .id(data.getId())
                            .topicId(data.getTopicId())
                            .content(data.getContent())
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return List.of();
        }
    }
    @Transactional(readOnly = true)
    public List<TopicResponseTo> getTopicsByFilters(List<String> markerNames, List<Long> markerIds,
                                                    String creatorLogin, String title, String content) {
        return topicRepo.findByFilters(markerNames, markerIds, creatorLogin, title, content)
                .stream()
                .map(mapper::toTopicResponseWithRelations)
                .collect(Collectors.toList());
    }

    public void checkOwnerOrAdmin(Long targetCreatorId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ForbiddenException("Authentication required");
        }

        String currentUserLogin = auth.getName();
        String userRole = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_CUSTOMER");

        if ("ROLE_ADMIN".equals(userRole)) {
            return;
        }

        Creator current = creatorRepo.findByLogin(currentUserLogin)
                .orElseThrow(() -> new NotFoundException("Creator with login " + currentUserLogin, null));

        if (!current.getId().equals(targetCreatorId)) {
            throw new ForbiddenException("Access denied: you can only modify your own data");
        }
    }

    public Creator getCurrentAuthenticatedCreator() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ForbiddenException("User not authenticated");
        }
        String login = auth.getName();
        return creatorRepo.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("Creator", null));
    }

    public void checkResourceAccess(Long resourceCreatorId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ForbiddenException("Authentication required");
        }
        String userRole = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_CUSTOMER");

        if ("ROLE_ADMIN".equals(userRole)) {
            return;
        }
        String currentLogin = auth.getName();
        Creator current = creatorRepo.findByLogin(currentLogin)
                .orElseThrow(() -> new NotFoundException("Creator", null));
        if (!current.getId().equals(resourceCreatorId)) {
            throw new ForbiddenException("Access denied to this resource");
        }
    }
}