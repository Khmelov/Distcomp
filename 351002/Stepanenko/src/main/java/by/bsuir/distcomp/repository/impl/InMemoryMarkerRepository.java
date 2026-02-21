package by.bsuir.distcomp.repository.impl;

import by.bsuir.distcomp.entity.Marker;
import by.bsuir.distcomp.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryMarkerRepository implements CrudRepository<Marker, Long> {
    private final Map<Long, Marker> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Marker save(Marker entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Marker> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Marker> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Marker update(Marker entity) {
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

    public List<Marker> findByTweetId(Long tweetId, Map<Long, Set<Long>> tweetToMarkers) {
        Set<Long> markerIds = tweetToMarkers.getOrDefault(tweetId, Collections.emptySet());
        return markerIds.stream()
                .map(this::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<Marker> findByName(String name) {
        return storage.values().stream()
                .filter(marker -> name.equals(marker.getName()))
                .collect(Collectors.toList());
    }
}
