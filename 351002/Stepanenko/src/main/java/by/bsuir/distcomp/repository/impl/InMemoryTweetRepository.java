package by.bsuir.distcomp.repository.impl;

import by.bsuir.distcomp.entity.Tweet;
import by.bsuir.distcomp.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTweetRepository implements CrudRepository<Tweet, Long> {
    private final Map<Long, Tweet> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Tweet save(Tweet entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Tweet> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Tweet> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Tweet update(Tweet entity) {
        if (entity.getId() == null || !storage.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Entity with id " + entity.getId() + " does not exist");
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    public List<Tweet> findByAuthorId(Long authorId) {
        return storage.values().stream()
                .filter(tweet -> authorId.equals(tweet.getAuthorId()))
                .collect(Collectors.toList());
    }

    public List<Tweet> findByMarkerIds(Set<Long> markerIds) {
        return storage.values().stream()
                .filter(tweet -> tweet.getMarkerIds() != null && 
                        tweet.getMarkerIds().stream().anyMatch(markerIds::contains))
                .collect(Collectors.toList());
    }

    public List<Tweet> findByMarkerName(String markerName, Map<Long, String> markerIdToName) {
        Set<Long> markerIds = markerIdToName.entrySet().stream()
                .filter(entry -> markerName.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        return findByMarkerIds(markerIds);
    }

    public List<Tweet> findByAuthorLogin(String authorLogin, Map<Long, String> authorIdToLogin) {
        Set<Long> authorIds = authorIdToLogin.entrySet().stream()
                .filter(entry -> authorLogin.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        return storage.values().stream()
                .filter(tweet -> authorIds.contains(tweet.getAuthorId()))
                .collect(Collectors.toList());
    }

    public List<Tweet> findByTitle(String title) {
        return storage.values().stream()
                .filter(tweet -> title.equals(tweet.getTitle()))
                .collect(Collectors.toList());
    }

    public List<Tweet> findByContent(String content) {
        return storage.values().stream()
                .filter(tweet -> tweet.getContent() != null && tweet.getContent().contains(content))
                .collect(Collectors.toList());
    }
}
