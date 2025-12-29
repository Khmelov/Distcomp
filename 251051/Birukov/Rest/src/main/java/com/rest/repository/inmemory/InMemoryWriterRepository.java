package com.rest.repository.inmemory;

import com.rest.entity.Writer;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryWriterRepository {
    
    private final Map<Long, Writer> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public Writer save(Writer writer) {
		if (writer == null) {
			throw new IllegalArgumentException("Writer cannot be null");
		}
		
        if (writer.getId() == null) {
            writer.setId(idGenerator.getAndIncrement());
        }
        storage.put(writer.getId(), writer);
        return writer;
    }
    
    public Optional<Writer> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }
    
    public List<Writer> findAll() {
        return new ArrayList<>(storage.values());
    }
    
    public Writer update(Writer writer) {
        if (!storage.containsKey(writer.getId())) {
            throw new RuntimeException("Writer not found with id: " + writer.getId());
        }
        storage.put(writer.getId(), writer);
        return writer;
    }
    
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
    
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
	
	public Optional<Writer> findByLogin(String login) {
        return storage.values().stream()
            .filter(writer -> writer.getLogin().equals(login))
            .findFirst();
    }
    
    public boolean existsByLogin(String login) {
        return storage.values().stream()
            .anyMatch(writer -> writer.getLogin().equals(login));
    }
}