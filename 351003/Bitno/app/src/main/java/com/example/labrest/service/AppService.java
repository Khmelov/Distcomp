package com.example.labrest.service;

import com.example.labrest.dto.*;
import com.example.labrest.exception.NotFoundException;
import com.example.labrest.mapper.AppMapper;
import com.example.labrest.model.*;
import com.example.labrest.repository.InMemoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppService {
    private final InMemoryRepository repo;
    private final AppMapper mapper;

    public AppService(InMemoryRepository repo, AppMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public CreatorResponseTo createCreator(CreatorRequestTo req) {
        return mapper.toCreatorResponse(repo.saveCreator(mapper.toCreator(req)));
    }
    public TopicResponseTo createTopic(TopicRequestTo req) {
        return mapper.toTopicResponse(repo.saveTopic(mapper.toTopic(req)));
    }
    public MarkerResponseTo createMarker(MarkerRequestTo req) {
        return mapper.toMarkerResponse(repo.saveMarker(mapper.toMarker(req)));
    }
    public NoticeResponseTo createNotice(NoticeRequestTo req) {
        return mapper.toNoticeResponse(repo.saveNotice(mapper.toNotice(req)));
    }

    public CreatorResponseTo getCreator(Long id) {
        return repo.getCreator(id).map(mapper::toCreatorResponse).orElseThrow(() -> new NotFoundException("Creator", id));
    }
    public TopicResponseTo getTopic(Long id) {
        return repo.getTopic(id).map(mapper::toTopicResponse).orElseThrow(() -> new NotFoundException("Topic", id));
    }
    public MarkerResponseTo getMarker(Long id) {
        return repo.getMarker(id).map(mapper::toMarkerResponse).orElseThrow(() -> new NotFoundException("Marker", id));
    }
    public NoticeResponseTo getNotice(Long id) {
        return repo.getNotice(id).map(mapper::toNoticeResponse).orElseThrow(() -> new NotFoundException("Notice", id));
    }

    public List<CreatorResponseTo> getAllCreators() {
        return repo.getAllCreators().stream().map(mapper::toCreatorResponse).collect(Collectors.toList());
    }
    public List<TopicResponseTo> getAllTopics() {
        return repo.getAllTopics().stream().map(mapper::toTopicResponse).collect(Collectors.toList());
    }
    public List<MarkerResponseTo> getAllMarkers() {
        return repo.getAllMarkers().stream().map(mapper::toMarkerResponse).collect(Collectors.toList());
    }
    public List<NoticeResponseTo> getAllNotices() {
        return repo.getAllNotices().stream().map(mapper::toNoticeResponse).collect(Collectors.toList());
    }

    public CreatorResponseTo updateCreator(Long id, CreatorRequestTo req) {
        Creator c = repo.getCreator(id).orElseThrow(() -> new NotFoundException("Creator", id));
        c.setLogin(req.getLogin()); c.setPassword(req.getPassword()); c.setFirstname(req.getFirstname()); c.setLastname(req.getLastname());
        return mapper.toCreatorResponse(repo.updateCreator(c));
    }
    public TopicResponseTo updateTopic(Long id, TopicRequestTo req) {
        Topic t = repo.getTopic(id).orElseThrow(() -> new NotFoundException("Topic", id));
        t.setCreatorId(req.getCreatorId()); t.setTitle(req.getTitle()); t.setContent(req.getContent()); t.setMarkerIds(req.getMarkerIds());
        return mapper.toTopicResponse(repo.updateTopic(t));
    }
    public MarkerResponseTo updateMarker(Long id, MarkerRequestTo req) {
        Marker m = repo.getMarker(id).orElseThrow(() -> new NotFoundException("Marker", id));
        m.setName(req.getName());
        return mapper.toMarkerResponse(repo.updateMarker(m));
    }
    public NoticeResponseTo updateNotice(Long id, NoticeRequestTo req) {
        Notice n = repo.getNotice(id).orElseThrow(() -> new NotFoundException("Notice", id));
        n.setTopicId(req.getTopicId()); n.setContent(req.getContent());
        return mapper.toNoticeResponse(repo.updateNotice(n));
    }

    public void deleteCreator(Long id) { repo.deleteCreator(id); }
    public void deleteTopic(Long id) { repo.deleteTopic(id); }
    public void deleteMarker(Long id) { repo.deleteMarker(id); }
    public void deleteNotice(Long id) { repo.deleteNotice(id); }

    public CreatorResponseTo getCreatorByTopicId(Long topicId) {
        return repo.getCreatorByTopicId(topicId).map(mapper::toCreatorResponse).orElseThrow(() -> new NotFoundException("Creator by Topic", topicId));
    }
    public List<MarkerResponseTo> getMarkersByTopicId(Long topicId) {
        return repo.getMarkersByTopicId(topicId).stream().map(mapper::toMarkerResponse).collect(Collectors.toList());
    }
    public List<NoticeResponseTo> getNoticesByTopicId(Long topicId) {
        return repo.getNoticesByTopicId(topicId).stream().map(mapper::toNoticeResponse).collect(Collectors.toList());
    }
}