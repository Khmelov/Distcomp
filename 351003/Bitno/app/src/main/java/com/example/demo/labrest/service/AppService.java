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
import com.example.demo.labrest.repository.NoticeRepository;
import com.example.demo.labrest.repository.TopicRepository;
import com.example.demo.labrest.dto.*;
import com.example.demo.labrest.model.*;
import com.example.demo.labrest.exception.NotFoundException;
import com.example.demo.labrest.mapper.AppMapper;
import com.example.demo.labrest.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppService {
    private final CreatorRepository creatorRepo;
    private final TopicRepository topicRepo;
    private final MarkerRepository markerRepo;
    private final NoticeRepository noticeRepo;
    private final AppMapper mapper;

    public AppService(CreatorRepository creatorRepo, TopicRepository topicRepo,
                      MarkerRepository markerRepo, NoticeRepository noticeRepo, AppMapper mapper) {
        this.creatorRepo = creatorRepo;
        this.topicRepo = topicRepo;
        this.markerRepo = markerRepo;
        this.noticeRepo = noticeRepo;
        this.mapper = mapper;
    }

    public CreatorResponseTo createCreator(CreatorRequestTo req) {
        if (creatorRepo.existsByLogin(req.getLogin())) {
            throw new ForbiddenException("Creator with login " + req.getLogin() + " already exists");
        }
        return mapper.toCreatorResponse(creatorRepo.save(mapper.toCreator(req)));
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
                    .map(id -> markerRepo.findById(id)
                            .orElseThrow(() -> new NotFoundException("Marker", id)))
                    .collect(Collectors.toSet());
            topic.setMarkers(markers);
        }
        if (req.getMarkers() != null && !req.getMarkers().isEmpty()) {
            Set<Marker> markers = req.getMarkers().stream()
                    .map(markerName -> markerRepo.findByName(markerName)
                            .orElseGet(() -> {
                                Marker newMarker = new Marker();
                                newMarker.setName(markerName);
                                markerRepo.save(newMarker);
                                return newMarker;
                            }))
                    .collect(Collectors.toSet());
            topic.setMarkers(markers);
        }

        return mapper.toTopicResponseWithRelations(topicRepo.save(topic));
    }

    public MarkerResponseTo createMarker(MarkerRequestTo req) {
        return mapper.toMarkerResponse(markerRepo.save(mapper.toMarker(req)));
    }

    public NoticeResponseTo createNotice(NoticeRequestTo req) {
        Notice notice = mapper.toNotice(req);
        Topic topic = topicRepo.findById(req.getTopicId())
                .orElseThrow(() -> new NotFoundException("Topic", req.getTopicId()));
        notice.setTopic(topic);
        return mapper.toNoticeResponse(noticeRepo.save(notice));
    }

    @Transactional(readOnly = true)
    public CreatorResponseTo getCreator(Long id) {
        return creatorRepo.findById(id)
                .map(mapper::toCreatorResponse)
                .orElseThrow(() -> new NotFoundException("Creator", id));
    }

    @Transactional(readOnly = true)
    public TopicResponseTo getTopic(Long id) {
        return topicRepo.findById(id)
                .map(mapper::toTopicResponseWithRelations)
                .orElseThrow(() -> new NotFoundException("Topic", id));
    }

    @Transactional(readOnly = true)
    public MarkerResponseTo getMarker(Long id) {
        return markerRepo.findById(id)
                .map(mapper::toMarkerResponse)
                .orElseThrow(() -> new NotFoundException("Marker", id));
    }

    @Transactional(readOnly = true)
    public NoticeResponseTo getNotice(Long id) {
        return noticeRepo.findById(id)
                .map(mapper::toNoticeResponse)
                .orElseThrow(() -> new NotFoundException("Notice", id));
    }

    @Transactional(readOnly = true)
    public List<CreatorResponseTo> getAllCreators(Pageable pageable) {
        return creatorRepo.findAll(pageable).stream().map(mapper::toCreatorResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopicResponseTo> getAllTopics(Pageable pageable) {
        return topicRepo.findAll(pageable).stream().map(mapper::toTopicResponseWithRelations).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MarkerResponseTo> getAllMarkers(Pageable pageable) {
        return markerRepo.findAll(pageable).stream().map(mapper::toMarkerResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseTo> getAllNotices(Pageable pageable) {
        return noticeRepo.findAll(pageable).stream().map(mapper::toNoticeResponse).collect(Collectors.toList());
    }

    public CreatorResponseTo updateCreator(Long id, CreatorRequestTo req) {
        Creator c = creatorRepo.findById(id).orElseThrow(() -> new NotFoundException("Creator", id));
        c.setLogin(req.getLogin()); c.setPassword(req.getPassword());
        c.setFirstname(req.getFirstname()); c.setLastname(req.getLastname());
        return mapper.toCreatorResponse(creatorRepo.save(c));
    }

    public TopicResponseTo updateTopic(Long id, TopicRequestTo req) {
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

        return mapper.toTopicResponseWithRelations(topicRepo.save(topic));
    }

    public MarkerResponseTo updateMarker(Long id, MarkerRequestTo req) {
        Marker m = markerRepo.findById(id).orElseThrow(() -> new NotFoundException("Marker", id));
        m.setName(req.getName());
        return mapper.toMarkerResponse(markerRepo.save(m));
    }

    public NoticeResponseTo updateNotice(Long id, NoticeRequestTo req) {
        Notice n = noticeRepo.findById(id).orElseThrow(() -> new NotFoundException("Notice", id));
        Topic topic = topicRepo.findById(req.getTopicId())
                .orElseThrow(() -> new NotFoundException("Topic", req.getTopicId()));
        n.setTopic(topic); n.setContent(req.getContent());
        return mapper.toNoticeResponse(noticeRepo.save(n));
    }

    @Transactional
    public void deleteCreator(Long id) {
        if (!creatorRepo.existsById(id)) {
            throw new NotFoundException("Creator", id);
        }
        creatorRepo.deleteById(id);
    }
    @Transactional
    public void deleteTopicAndOrphanMarkers(Long topicId) {
        Topic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Topic",  topicId));

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

    @Transactional
    public void deleteMarker(Long id) {
        if (!markerRepo.existsById(id)) {
            throw new NotFoundException("Marker", id);
        }
        markerRepo.deleteById(id);
    }

    @Transactional
    public void deleteNotice(Long id) {
        if (!noticeRepo.existsById(id)) {
            throw new NotFoundException("Notice", id);
        }
        noticeRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public CreatorResponseTo getCreatorByTopicId(Long topicId) {
        Topic t = topicRepo.findById(topicId).orElseThrow(() -> new NotFoundException("Topic", topicId));
        return mapper.toCreatorResponse(t.getCreator());
    }

    @Transactional(readOnly = true)
    public List<MarkerResponseTo> getMarkersByTopicId(Long topicId) {
        Topic t = topicRepo.findById(topicId).orElseThrow(() -> new NotFoundException("Topic", topicId));
        return t.getMarkers().stream().map(mapper::toMarkerResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseTo> getNoticesByTopicId(Long topicId) {
        return noticeRepo.findByTopic_Id(topicId).stream().map(mapper::toNoticeResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopicResponseTo> getTopicsByFilters(List<String> markerNames, List<Long> markerIds,
                                                    String creatorLogin, String title, String content) {
        return topicRepo.findByFilters(markerNames, markerIds, creatorLogin, title, content)
                .stream().map(mapper::toTopicResponseWithRelations).collect(Collectors.toList());
    }
}