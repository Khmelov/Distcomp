// InMemoryMarkRepository.java
package com.example.publisher.repository;

import org.springframework.stereotype.Repository;
import com.example.publisher.model.Mark;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryMarkRepository implements CrudRepository<Mark, Long> {
    private final Map<Long, Mark> marks = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<Mark> findAll() {
        return new ArrayList<>(marks.values());
    }

    @Override
    public Optional<Mark> findById(Long id) {
        return Optional.ofNullable(marks.get(id));
    }

    @Override
    public Mark save(Mark mark) {
        if (mark.getId() == null) {
            mark.setId(idCounter.getAndIncrement());
        }
        marks.put(mark.getId(), mark);
        return mark;
    }

    @Override
    public Mark update(Mark mark) {
        if (mark.getId() == null || !marks.containsKey(mark.getId())) {
            throw new IllegalArgumentException("Mark not found with id: " + mark.getId());
        }
        mark.setModified(java.time.LocalDateTime.now());
        marks.put(mark.getId(), mark);
        return mark;
    }

    @Override
    public boolean deleteById(Long id) {
        return marks.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return marks.containsKey(id);
    }

    public Optional<Mark> findByName(String name) {
        return marks.values().stream()
                .filter(mark -> mark.getName().equals(name))
                .findFirst();
    }
}