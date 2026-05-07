package com.example.labrest.repository;

import com.example.labrest.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryRepository {
    private final Map<Long, Creator> creators = new ConcurrentHashMap<>();
    private final Map<Long, Topic> topics = new ConcurrentHashMap<>();
    private final Map<Long, Marker> markers = new ConcurrentHashMap<>();
    private final Map<Long, Notice> notices = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @PostConstruct
    public void init() {
        creators.put(idGen.get(), new Creator(idGen.get(), "misabitnol@gmail.com", "pass1234", "Михаил", "Битно"));
    }

    public Creator saveCreator(Creator c) { c.setId(idGen.get()); creators.put(idGen.getAndIncrement(), c); return c; }
    public Topic saveTopic(Topic t) { t.setId(idGen.get()); t.setCreated(LocalDateTime.now()); t.setModified(LocalDateTime.now()); topics.put(idGen.getAndIncrement(), t); return t; }
    public Marker saveMarker(Marker m) { m.setId(idGen.get()); markers.put(idGen.getAndIncrement(), m); return m; }
    public Notice saveNotice(Notice n) { n.setId(idGen.get()); notices.put(idGen.getAndIncrement(), n); return n; }

    public Creator updateCreator(Creator c) { creators.put(c.getId(), c); return c; }
    public Topic updateTopic(Topic t) { t.setModified(LocalDateTime.now()); topics.put(t.getId(), t); return t; }
    public Marker updateMarker(Marker m) { markers.put(m.getId(), m); return m; }
    public Notice updateNotice(Notice n) { notices.put(n.getId(), n); return n; }

    public void deleteCreator(Long id) { creators.remove(id); }
    public void deleteTopic(Long id) { topics.remove(id); }
    public void deleteMarker(Long id) { markers.remove(id); }
    public void deleteNotice(Long id) { notices.remove(id); }

    public Optional<Creator> getCreator(Long id) { return Optional.ofNullable(creators.get(id)); }
    public Optional<Topic> getTopic(Long id) { return Optional.ofNullable(topics.get(id)); }
    public Optional<Marker> getMarker(Long id) { return Optional.ofNullable(markers.get(id)); }
    public Optional<Notice> getNotice(Long id) { return Optional.ofNullable(notices.get(id)); }

    public Collection<Creator> getAllCreators() { return creators.values(); }
    public Collection<Topic> getAllTopics() { return topics.values(); }
    public Collection<Marker> getAllMarkers() { return markers.values(); }
    public Collection<Notice> getAllNotices() { return notices.values(); }

    public Optional<Creator> getCreatorByTopicId(Long topicId) {
        return Optional.ofNullable(topics.get(topicId))
                .map(Topic::getCreatorId)
                .flatMap(creatorId -> Optional.ofNullable(creators.get(creatorId)));
    }
    public List<Marker> getMarkersByTopicId(Long topicId) {
        Topic t = topics.get(topicId);
        if (t == null) return new ArrayList<>();
        return t.getMarkerIds().stream().map(markers::get).filter(Objects::nonNull).toList();
    }
    public List<Notice> getNoticesByTopicId(Long topicId) {
        return notices.values().stream().filter(n -> n.getTopicId().equals(topicId)).toList();
    }
}