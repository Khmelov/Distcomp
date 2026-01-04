package com.example.entitiesapp.repositories.impl;

import com.example.entitiesapp.entities.Writer;
import com.example.entitiesapp.repositories.WriterRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryWriterRepository implements WriterRepository {
    private final Map<Long, Writer> writers = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Writer save(Writer writer) {
        if (writer.getId() == null) {
            writer.setId(idCounter.getAndIncrement());
            writer.setCreated(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        }
        writer.setModified(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        writers.put(writer.getId(), writer);
        return writer;
    }

    @Override
    public Optional<Writer> findById(Long id) {
        return Optional.ofNullable(writers.get(id));
    }

    @Override
    public List<Writer> findAll() {
        return new ArrayList<>(writers.values());
    }

    @Override
    public Writer update(Writer writer) {
        if (!writers.containsKey(writer.getId())) {
            throw new IllegalArgumentException("Writer not found with id: " + writer.getId());
        }
        writer.setModified(new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        writers.put(writer.getId(), writer);
        return writer;
    }

    @Override
    public boolean deleteById(Long id) {
        return writers.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return writers.containsKey(id);
    }

    @Override
    public Optional<Writer> findByLogin(String login) {
        return writers.values().stream()
                .filter(writer -> writer.getLogin().equals(login))
                .findFirst();
    }
}